package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class FollowRequestDoesntExist extends ApiException{
    public FollowRequestDoesntExist(){
        super(HttpStatus.NOT_FOUND, "Follow request doesn't exists!");
    }
}
