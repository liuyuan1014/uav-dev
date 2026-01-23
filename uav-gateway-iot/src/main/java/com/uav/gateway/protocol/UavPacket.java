package com.uav.gateway.protocol;

import lombok.Data;

@Data
public class UavPacket {
    private short magic;
    private byte version;
    private byte command;
    private int length;
    private String body;
}