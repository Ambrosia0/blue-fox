package com.ambrosia.profile_service.exception.api.blacklist;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class ExceededNumberOfBlacklistedException extends ApiException{
    public ExceededNumberOfBlacklistedException(){
        super(HttpStatus.BAD_REQUEST, "Reached maximum number of blacklisted users!");
    }
}
