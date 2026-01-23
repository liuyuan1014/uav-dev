package com.uav.gateway.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class UavDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节来读取头部 (2 + 1 + 1 + 4 = 8 bytes)
        if (in.readableBytes() < 8) {
            return;
        }

        // 标记读取位置，以便在数据不完整时回退
        in.markReaderIndex();

        // 读取魔数
        short magic = in.readShort();
        if (magic != (short) 0xACED) {
            System.out.println("Invalid magic number: " + magic);
            ctx.close();
            return;
        }

        // 读取版本
        byte version = in.readByte();

        // 读取命令
        byte command = in.readByte();

        // 读取长度
        int length = in.readInt();

        // 检查是否有足够的字节来读取body
        if (in.readableBytes() < length) {
            // 长度不够，重置读取位置，等待更多数据
            in.resetReaderIndex();
            return;
        }

        // 读取body
        byte[] bodyBytes = new byte[length];
        in.readBytes(bodyBytes);
        String body = new String(bodyBytes, StandardCharsets.UTF_8);

        // 创建UavPacket对象
        UavPacket packet = new UavPacket();
        packet.setMagic(magic);
        packet.setVersion(version);
        packet.setCommand(command);
        packet.setLength(length);
        packet.setBody(body);

        // 添加到输出列表
        out.add(packet);
    }
}