package com.uav.gateway.protocol;

/**
 * 无人机协议命令类型常量
 * 定义了客户端与服务器之间通信的命令类型
 */
public class UavCommandType {
    
    /**
     * 登录命令 - 设备首次连接时发送
     */
    public static final byte LOGIN = 1;
    
    /**
     * 心跳/遥测数据命令 - 设备定期发送状态数据
     */
    public static final byte HEARTBEAT = 2;
    
    /**
     * 控制指令命令 - 服务器向设备下发控制指令
     */
    public static final byte CONTROL = 3;
    
    private UavCommandType() {
        // 工具类，禁止实例化
    }
}