package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class CommentDoesntRelatedException extends ApiException{
    public CommentDoesntRelatedException(){
        super(HttpStatus.BAD_REQUEST, "Comment doesn't related to community/doesn't exist!");
    }
}
