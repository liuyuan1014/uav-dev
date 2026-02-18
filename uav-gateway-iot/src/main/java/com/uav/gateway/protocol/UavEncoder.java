package com.uav.gateway.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * 无人机协议编码器
 * 将 UavPacket 对象编码为字节流
 */
public class UavEncoder extends MessageToByteEncoder<UavPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, UavPacket packet, ByteBuf out) throws Exception {
        // 1. 写入魔数 (2字节)
        out.writeShort(packet.getMagic());
        
        // 2. 写入版本号 (1字节)
        out.writeByte(packet.getVersion());
        
        // 3. 写入命令类型 (1字节)
        out.writeByte(packet.getCommand());
        
        // 4. 写入消息体长度 (4字节)
        byte[] bodyBytes = packet.getBody().getBytes(StandardCharsets.UTF_8);
        out.writeInt(bodyBytes.length);
        
        // 5. 写入消息体
        out.writeBytes(bodyBytes);
    }
}