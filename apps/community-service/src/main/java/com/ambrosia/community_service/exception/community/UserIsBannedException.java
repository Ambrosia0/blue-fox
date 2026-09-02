package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class UserIsBannedException extends ApiException{
    public UserIsBannedException(){
        super(HttpStatus.BAD_REQUEST, "User is banned!");
    }
}
