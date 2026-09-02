package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class CommunityDoesntExistException extends ApiException{
    public CommunityDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Community doesn't exist!");
    }
}
