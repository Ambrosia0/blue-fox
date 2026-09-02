package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class AlreadyFollowedException extends ApiException{
    public AlreadyFollowedException(){
        super(HttpStatus.BAD_REQUEST, "Already followed to user/community!");
    }
}
