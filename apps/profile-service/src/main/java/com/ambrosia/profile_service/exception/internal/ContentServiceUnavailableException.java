package com.ambrosia.profile_service.exception.internal;

public class ContentServiceUnavailableException extends RuntimeException{
    public ContentServiceUnavailableException(){
        super("Unavailable!");
    }
}
