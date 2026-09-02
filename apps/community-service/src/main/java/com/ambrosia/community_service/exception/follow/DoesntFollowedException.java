package com.ambrosia.community_service.exception.follow;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class DoesntFollowedException extends ApiException{
    public DoesntFollowedException(){
        super(HttpStatus.BAD_REQUEST, "Doesn't followed to user/community!");
    }
}
