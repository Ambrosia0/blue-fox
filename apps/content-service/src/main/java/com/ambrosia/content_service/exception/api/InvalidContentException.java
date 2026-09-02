package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class InvalidContentException extends ApiException{
    public InvalidContentException(){
        super(HttpStatus.BAD_REQUEST, "Invalid post content!");
    }
}
