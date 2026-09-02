package com.ambrosia.profile_service.exception.api.user;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class UsernameAlreadyClaimedException extends ApiException{
    public UsernameAlreadyClaimedException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }

    public UsernameAlreadyClaimedException(){
        super(HttpStatus.BAD_REQUEST, "Username already claimed!");
    }
}
