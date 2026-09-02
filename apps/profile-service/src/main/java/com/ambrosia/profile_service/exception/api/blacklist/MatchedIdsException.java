package com.ambrosia.profile_service.exception.api.blacklist;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class MatchedIdsException extends ApiException{
    public MatchedIdsException(){
        super(HttpStatus.BAD_REQUEST, "Ids must not be the same!");
    }
}
