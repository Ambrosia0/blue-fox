package com.ambrosia.community_service.community.utils;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SupportedFileTypes {
    WEBP("image/webp", ".webp"),
    JPG("image/jpg", ".jpg"),
    JPEG("image/jpeg", ".jpeg"),
    PNG("image/png", ".png");

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
