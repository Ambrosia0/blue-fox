package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class NotEnoughPermissionsException extends ApiException{
    public NotEnoughPermissionsException(){
        super(HttpStatus.FORBIDDEN, "Not enough permissions!");
    }
}
