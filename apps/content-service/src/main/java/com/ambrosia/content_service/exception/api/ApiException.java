package com.ambrosia.content_service.exception.api;

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
