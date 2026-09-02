package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class UserBannedException extends ApiException{
    public UserBannedException(){
        super(HttpStatus.FORBIDDEN, "You are banned in this community!");
    }
}
