package com.ambrosia.profile_service.exception.api.user;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class UserIsDisabledException extends ApiException{
    public UserIsDisabledException(){
        super(HttpStatus.BAD_REQUEST, "User is disabled!");
    }
}
