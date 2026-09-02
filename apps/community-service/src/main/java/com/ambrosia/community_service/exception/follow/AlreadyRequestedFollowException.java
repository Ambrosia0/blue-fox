package com.ambrosia.community_service.exception.follow;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class AlreadyRequestedFollowException extends ApiException{
    public AlreadyRequestedFollowException(){
        super(HttpStatus.BAD_REQUEST, "Already requested follow!");
    }
}
