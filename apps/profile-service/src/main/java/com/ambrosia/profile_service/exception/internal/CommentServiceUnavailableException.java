package com.ambrosia.profile_service.exception.internal;

public class CommentServiceUnavailableException extends RuntimeException{
    public CommentServiceUnavailableException(){
        super("Unavailable!");
    }
}
