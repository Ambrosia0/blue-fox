package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class CommunityDoesntExistException extends ApiException{
    public CommunityDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Community doesn't exist!");
    }
}
