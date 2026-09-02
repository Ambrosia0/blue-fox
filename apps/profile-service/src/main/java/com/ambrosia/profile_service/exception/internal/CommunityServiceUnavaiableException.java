package com.ambrosia.profile_service.exception.internal;

public class CommunityServiceUnavaiableException extends RuntimeException{
    public CommunityServiceUnavaiableException(){
        super("Unavailable!");
    }
}
