package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class CollaborationApiEditException extends ApiException{
    public CollaborationApiEditException(){
        super(HttpStatus.BAD_REQUEST, "Collaboration post must be edited through collaboration api!");
    }
}
