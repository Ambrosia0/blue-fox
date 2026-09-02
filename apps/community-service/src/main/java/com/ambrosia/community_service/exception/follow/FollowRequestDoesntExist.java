package com.ambrosia.community_service.exception.follow;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class FollowRequestDoesntExist extends ApiException{
    public FollowRequestDoesntExist(){
        super(HttpStatus.NOT_FOUND, "Follow request doesn't exists!");
    }
}
