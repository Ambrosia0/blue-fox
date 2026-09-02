package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private HttpStatus status;
    
    public ApiException(HttpStatus status, String message){
        super(message);
        this.status = status;
    }
}
