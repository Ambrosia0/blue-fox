package com.ambrosia.collaboration_service.utils;

public enum NotificationCodes {
    ATTACHMENT_CREATE((byte)0x01),
    ATTACHMENT_DELETE((byte)0x02);

    private byte code;

    private NotificationCodes(byte code){
        this.code = code;
    }

    public byte getCode(){
        return this.code;
    }
}
