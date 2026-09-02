package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class NotEnoughPermissionsException extends ApiException {
    public NotEnoughPermissionsException(){
        super(HttpStatus.FORBIDDEN, "Not enough permissions!");
    }
}
