package com.ambrosia.community_service.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private final HttpStatus status;
    
    protected ApiException(HttpStatus status, String message){
        super(message);
        this.status = status;
    }
}
