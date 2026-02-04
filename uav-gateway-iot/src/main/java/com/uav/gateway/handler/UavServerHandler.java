package com.uav.gateway.handler;

import com.uav.gateway.config.NettyConnectManageService;
import com.uav.gateway.config.SpringContextUtil;
import com.uav.gateway.impl.UavCommandServiceGatewayImpl;
import com.uav.gateway.protocol.UavPacket;
import com.uav.gateway.producer.UavTelemetryProducer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import org.springframework.stereotype.Service;

// @Sharable注解表示这个类是共享的，多个线程可以共享同一个实例
@Service
@Sharable
//继承SimpleChannelInboundHandler，会自动将UavPacket里的资源释放掉，因为ByteBuf是占用堆外内存的，所以需要手动释放掉
public class UavServerHandler extends SimpleChannelInboundHandler<UavPacket> {

    private final UavTelemetryProducer uavTelemetryProducer;

    public UavServerHandler(UavTelemetryProducer uavTelemetryProducer) {
        this.uavTelemetryProducer = uavTelemetryProducer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新设备连接: " + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    // Netty 是基于事件的，当一个数据包到达时，会调用这个方法，这里不能写死循环或者sleep，会堵塞住整个线程，导致其他无人机的消息处理不过来
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UavPacket packet) throws Exception {
        // 添加详细日志来调试消息接收
        System.out.println("=== 收到消息 ===");
        System.out.println("远程地址: " + ctx.channel().remoteAddress());
        System.out.println("命令类型: " + packet.getCommand());
        System.out.println("消息体: " + packet.getBody());
        System.out.println("消息长度: " + (packet.getBody() != null ? packet.getBody().length() : 0));
        
        byte command = packet.getCommand();

        switch (command) {
            case 1: // LOGIN
                System.out.println("设备登录请求: " + packet.getBody());
                handleLogin(ctx, packet.getBody());
                break;
            case 2: // HEARTBEAT
                System.out.println("收到心跳");
                
                // 从心跳数据中解析设备ID并注册连接（如果尚未注册）
                String heartbeatDeviceId = parseDeviceIdFromHeartbeat(packet.getBody());
                if (heartbeatDeviceId != null && !heartbeatDeviceId.isEmpty()) {
                    // 获取连接管理服务并注册设备连接
                    NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
                    if (connectManageService.getChannel(heartbeatDeviceId) == null) {
                        connectManageService.addChannel(heartbeatDeviceId, ctx.channel());
                    }
                }
                
                // 调用Kafka生产者发送遥测数据
                try {
                    System.out.println("开始发送遥测数据到Kafka...");
                    uavTelemetryProducer.sendTelemetry(packet);
                } catch (Exception e) {
                    System.err.println("发送Kafka消息失败: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case 3: // COMMAND RESPONSE (来自无人机对指令的响应)
                System.out.println("收到指令响应");
                // 这里可以处理无人机对指令的响应
                break;
            default:
                System.out.println("未知命令: " + command);
                break;
        }
        System.out.println("=== 消息处理完成 ===");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("处理客户端消息时发生异常: " + cause.getMessage());
        cause.printStackTrace();
        
        // 异常时移除连接
        NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
        connectManageService.removeChannel(ctx.channel());
        
        System.out.println("设备断开连接（异常）: " + ctx.channel().remoteAddress());
        ctx.close();
    }

    // 当无人机断网、掉线、或者主动断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("设备断开连接: " + ctx.channel().remoteAddress());
        
        // 从Channel上获取设备ID
        String deviceId = ctx.channel().attr(NettyConnectManageService.DEVICE_ID_KEY).get();
        
        // 移除连接
        NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
        connectManageService.removeChannel(ctx.channel());
        
        if (deviceId != null) {
            System.out.println("设备断开连接: " + deviceId);
        }
        
        super.channelInactive(ctx);
    }
    
    /**
     * 处理设备登录请求
     * JSON格式: {"deviceId":"UAV-001", "authCode":"abc123..."}
     */
    private void handleLogin(ChannelHandlerContext ctx, String loginData) {
        try {
            // 解析设备ID和authCode
            String deviceId = extractFieldFromJson(loginData, "deviceId");
            String authCode = extractFieldFromJson(loginData, "authCode");
            
            if (deviceId == null || deviceId.isEmpty()) {
                System.err.println("登录失败: 缺少设备ID");
                ctx.close();
                return;
            }
            
            if (authCode == null || authCode.isEmpty()) {
                System.err.println("登录失败: 缺少鉴权码 - " + deviceId);
                ctx.close();
                return;
            }
            
            // 调用验证服务
            UavCommandServiceGatewayImpl commandService = SpringContextUtil.getBean(UavCommandServiceGatewayImpl.class);
            boolean isValid = commandService.validateLogin(deviceId, authCode);
            
            if (isValid) {
                // 验证通过，注册连接
                NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
                connectManageService.addChannel(deviceId, ctx.channel());
                System.out.println("✅ 设备登录成功: " + deviceId);
            } else {
                // 验证失败，断开连接
                System.err.println("❌ 设备鉴权失败，断开连接: " + deviceId);
                ctx.close();
            }
        } catch (Exception e) {
            System.err.println("处理登录请求异常: " + e.getMessage());
            e.printStackTrace();
            ctx.close();
        }
    }
    
    /**
     * 从登录数据中解析设备ID
     * 
     * @param loginData 登录数据
     * @return 设备ID
     */
    private String parseDeviceIdFromLogin(String loginData) {
        // 如果登录数据是JSON格式，尝试从中解析设备ID
        if (loginData != null && loginData.trim().startsWith("{")) {
            return extractFieldFromJson(loginData.trim(), "deviceId");
        }
        // 否则直接返回登录数据作为设备ID（假设是纯文本格式）
        return loginData != null ? loginData.trim() : null;
    }
    
    /**
     * 从心跳数据中解析设备ID
     * 
     * @param heartbeatData 心跳数据
     * @return 设备ID
     */
    private String parseDeviceIdFromHeartbeat(String heartbeatData) {
        // 心跳数据通常是JSON格式，从中提取设备ID
        return extractFieldFromJson(heartbeatData, "deviceId");
    }
    
    /**
     * 从JSON字符串中提取指定字段的值
     *
     * @param json JSON字符串
     * @param fieldName 字段名
     * @return 字段值
     */
    private String extractFieldFromJson(String json, String fieldName) {
        if (json == null || json.trim().isEmpty()) {
            System.err.println("JSON为空");
            return null;
        }
        
        // 打印完整的JSON用于调试
        System.out.println("=== 开始解析JSON ===");
        System.out.println("JSON内容: " + json);
        System.out.println("JSON长度: " + json.length());
        System.out.println("查找字段: " + fieldName);
        
        try {
            // 构造要查找的字段模式："fieldName":
            String pattern = "\"" + fieldName + "\"";
            int fieldStart = json.indexOf(pattern);
            
            if (fieldStart == -1) {
                // 尝试小写
                pattern = "\"" + fieldName.toLowerCase() + "\"";
                fieldStart = json.indexOf(pattern);
                if (fieldStart == -1) {
                    System.err.println("❌ 未找到字段: " + fieldName);
                    return null;
                }
            }
            
            System.out.println("✓ 找到字段位置: " + fieldStart);
            
            // 找到字段名后的冒号
            int colonIndex = json.indexOf(":", fieldStart + pattern.length());
            if (colonIndex == -1) {
                System.err.println("❌ 字段 " + fieldName + " 后未找到冒号");
                return null;
            }
            
            System.out.println("✓ 找到冒号位置: " + colonIndex);
            
            // 跳过冒号后的空格和引号，找到值的真正起始位置
            int valueStart = colonIndex + 1;
            while (valueStart < json.length() &&
                   (Character.isWhitespace(json.charAt(valueStart)) || json.charAt(valueStart) == '"')) {
                if (json.charAt(valueStart) == '"') {
                    valueStart++; // 跳过起始引号
                    break;
                }
                valueStart++;
            }
            
            if (valueStart >= json.length()) {
                System.err.println("❌ 字段 " + fieldName + " 的值为空");
                return null;
            }
            
            System.out.println("✓ 值起始位置: " + valueStart);
            System.out.println("✓ 从位置" + valueStart + "开始的内容: " + json.substring(valueStart, Math.min(valueStart + 50, json.length())));
            
            // 找到值的结束位置（引号、逗号或右花括号）
            int valueEnd = valueStart;
            while (valueEnd < json.length()) {
                char c = json.charAt(valueEnd);
                if (c == '"' || c == ',' || c == '}') {
                    break;
                }
                valueEnd++;
            }
            
            if (valueEnd > valueStart) {
                String value = json.substring(valueStart, valueEnd);
                System.out.println("✅ 成功解析字段 " + fieldName + " = " + value);
                System.out.println("=== 解析完成 ===\n");
                return value;
            } else {
                System.err.println("❌ 无法提取字段值");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 解析JSON异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}