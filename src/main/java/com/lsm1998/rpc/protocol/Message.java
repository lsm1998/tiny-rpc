package com.lsm1998.rpc.protocol;

import lombok.Data;
import lombok.Getter;

@Data
public class Message {
    public static final byte[] MAGIC_NUMBER = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};

    private byte[] logic;

    private byte version;

    private byte messageType;

    private int length;

    private byte[] body;

    @Getter
    public enum MessageType {
        REQUEST((byte) 0x01),
        RESPONSE((byte) 0x02);

        private final byte value;

        MessageType(byte value) {
            this.value = value;
        }
    }
}
