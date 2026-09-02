package com.ambrosia.community_service.exception.follow;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class AlreadyFollowedException extends ApiException{
    public AlreadyFollowedException(){
        super(HttpStatus.BAD_REQUEST, "Already followed to user/community!");
    }
}
