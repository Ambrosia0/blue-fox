package com.ambrosia.content_service.post.exception;

import org.springframework.http.HttpStatus;

import com.ambrosia.content_service.exception.api.ApiException;

public class PostDoesntLikedException extends ApiException{
    public PostDoesntLikedException(){
        super(HttpStatus.BAD_REQUEST, "Post doesn't liked!");
    }
}
