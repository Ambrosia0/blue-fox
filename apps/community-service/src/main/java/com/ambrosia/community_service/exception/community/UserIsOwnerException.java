package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;


public class UserIsOwnerException extends ApiException{
    public UserIsOwnerException(){
        super(HttpStatus.BAD_REQUEST, "User is community owner!");
    }
}
