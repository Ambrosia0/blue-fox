package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class DoesntFollowedException extends ApiException{
    public DoesntFollowedException(){
        super(HttpStatus.BAD_REQUEST, "Doesn't followed to user/community!");
    }
}
