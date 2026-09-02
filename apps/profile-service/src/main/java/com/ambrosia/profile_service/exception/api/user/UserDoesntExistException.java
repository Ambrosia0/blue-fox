package com.ambrosia.profile_service.exception.api.user;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class UserDoesntExistException extends ApiException{
    public UserDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "User doesn't exist!");
    }
}
