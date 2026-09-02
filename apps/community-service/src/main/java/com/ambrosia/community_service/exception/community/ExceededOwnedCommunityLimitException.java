package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class ExceededOwnedCommunityLimitException extends ApiException{
    public ExceededOwnedCommunityLimitException(){
        super(HttpStatus.BAD_REQUEST, "Exceeded number of owned communities!");
    }
}
