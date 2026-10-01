package com.ambrosia.collaboration_service.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MessageCodes {
    MESSAGE((byte)0x01),
    CURSOR((byte)0x02),
    USER_JOIN((byte)0x03),
    USER_LEAVE((byte)0x04),
    CHAT_MESSAGE((byte)0x05),
    SYNC((byte)0x06),
    SYNC_RESP((byte)0x07),
    SAVE((byte)0x08),
    ABORT((byte)0x09),
    STATE((byte)0x0A),
    NOTIFICATION((byte)0x0B);

    public static Map<Byte, MessageCodes> CODES = Arrays
            .stream(values())
            .collect(Collectors.toUnmodifiableMap(t -> t.getCode(), t -> t));
            
    private byte code;

    private MessageCodes(byte code){
        this.code = code;
    }

    public byte getCode(){
        return code;
    }
}
