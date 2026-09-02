package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class UserBannedException extends ApiException{
    public UserBannedException(){
        super(HttpStatus.FORBIDDEN, "You are banned in this community!");
    }
}
