package com.ambrosia.content_service.post.exception;

import org.springframework.http.HttpStatus;

import com.ambrosia.content_service.exception.api.ApiException;

public class PostLikedException extends ApiException{
    public PostLikedException(){
        super(HttpStatus.BAD_REQUEST, "Post already liked!");
    }
}
