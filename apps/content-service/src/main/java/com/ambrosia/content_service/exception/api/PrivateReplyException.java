package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class PrivateReplyException extends ApiException {
    public PrivateReplyException(){
        super(HttpStatus.BAD_REQUEST, "Can't reply from private community!");
    }
}
