package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class InvalidPostVersionException extends ApiException{
    public InvalidPostVersionException(){
        super(HttpStatus.CONFLICT, "Invalid post version!");
    }
}
