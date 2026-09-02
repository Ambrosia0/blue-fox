package com.ambrosia.profile_service.exception.internal;

public class IdpServiceUnavailableException extends RuntimeException {
    public IdpServiceUnavailableException(){
        super("Unavailable");
    }
}
