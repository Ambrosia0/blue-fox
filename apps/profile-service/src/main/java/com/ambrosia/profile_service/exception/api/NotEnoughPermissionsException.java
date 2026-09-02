package com.ambrosia.profile_service.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class NotEnoughPermissionsException extends ApiException{
    public NotEnoughPermissionsException(){
        super(HttpStatus.FORBIDDEN, "Not enough permissions!");
    }
}
