package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class DoesntFollowedOnPrivateCommunityException extends ApiException{
    public DoesntFollowedOnPrivateCommunityException(){
        super(HttpStatus.BAD_REQUEST, "Doesn't followed on private community!");
    }
}
