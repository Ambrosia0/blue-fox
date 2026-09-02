package com.ambrosia.profile_service.exception.api.user;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class UserDoesntBannedException extends ApiException{
    public UserDoesntBannedException(){
        super(HttpStatus.BAD_REQUEST, "User doesn't banned!");
    }
}
