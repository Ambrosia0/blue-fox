package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;


public class NotEnoughPermissionsException extends ApiException{
    public NotEnoughPermissionsException(){
        super(HttpStatus.FORBIDDEN, "Not enough permissions!");
    }
}
