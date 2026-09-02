package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class CommentOrPostDoesntExistException extends ApiException{
    public CommentOrPostDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Post/parent comment doesn't exist!");
    }
}
