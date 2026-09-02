package com.ambrosia.content_service.attachment.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SupportedFileTypes {
    WEBP("image/webp", ".webp"),
    JPG("image/jpg", ".jpg"),
    JPEG("image/jpeg", ".jpeg"),
    PNG("image/png", ".png");

    @JsonValue
    private String mimeType;
    
    private String ext;

    private SupportedFileTypes(String mimeType, String ext){
        this.mimeType = mimeType;
        this.ext = ext;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension(){
        return ext;
    }

    @JsonCreator
    public static SupportedFileTypes fromMimeType(String mimeType){
        if(mimeType == null)
            return null;
        for(SupportedFileTypes type: values()){
            if(type.mimeType.equals(mimeType))
                return type;
        }
        return null;
    }
}
