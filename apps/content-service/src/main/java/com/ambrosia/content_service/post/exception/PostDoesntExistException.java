package com.ambrosia.content_service.post.exception;

import org.springframework.http.HttpStatus;

import com.ambrosia.content_service.exception.api.ApiException;

public class PostDoesntExistException extends ApiException{
    public PostDoesntExistException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }

    public PostDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Post doesn't exist!");
    }
}
