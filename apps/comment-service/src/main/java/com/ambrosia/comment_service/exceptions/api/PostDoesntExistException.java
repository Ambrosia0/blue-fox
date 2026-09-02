package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class PostDoesntExistException extends ApiException{
    public PostDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Post doesn't exist!");
    }
}
