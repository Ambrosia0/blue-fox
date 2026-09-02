package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;


public class UserDoesntExistException extends ApiException{
    public UserDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "User doesn't exist!");
    }
}
