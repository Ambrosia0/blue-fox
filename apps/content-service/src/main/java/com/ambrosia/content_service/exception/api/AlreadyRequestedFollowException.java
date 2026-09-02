package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class AlreadyRequestedFollowException extends ApiException{
    public AlreadyRequestedFollowException(){
        super(HttpStatus.BAD_REQUEST, "Already requested follow!");
    }
}
