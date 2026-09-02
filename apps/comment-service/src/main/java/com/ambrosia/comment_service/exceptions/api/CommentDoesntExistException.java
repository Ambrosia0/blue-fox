package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class CommentDoesntExistException extends ApiException{
    public CommentDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Comment doesn't exist!");
    }
}
