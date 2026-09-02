package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;


public class UserIsModeratorException extends ApiException{
    public UserIsModeratorException(){
        super(HttpStatus.BAD_REQUEST, "User is moderator!");
    }
}
