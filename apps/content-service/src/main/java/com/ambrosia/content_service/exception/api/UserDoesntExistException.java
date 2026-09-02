package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class UserDoesntExistException extends ApiException{
    public UserDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "User doesn't exist!");
    }
}
