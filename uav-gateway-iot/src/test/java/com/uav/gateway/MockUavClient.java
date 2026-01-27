package com.uav.gateway;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MockUavClient {
    public static void main(String[] args) throws Exception {
        // 替换为你的网关 IP 和端口
        String host = "127.0.0.1";
        int port = 8090;

        String json = "{\"deviceId\": \"uav-test-001\", \"lat\": 30.6586, \"lon\": 104.0648, \"altitude\": 120.5, \"speed\": 15.2, \"battery\": 88, \"timestamp\": 1706256000000}";
        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("已连接到网关...");

            // 1. 先写 4 字节的长度头 (LengthFieldBasedFrameDecoder 的要求)
            out.writeInt(data.length);
            // 2. 再写 JSON 数据
            out.write(data);
            out.flush();

            System.out.println("发送成功: " + json);

            // 保持连接一会，模拟设备在线
            Thread.sleep(5000);
        }
    }

}
