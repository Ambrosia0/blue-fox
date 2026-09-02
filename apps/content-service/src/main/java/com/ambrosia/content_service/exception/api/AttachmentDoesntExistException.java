package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class AttachmentDoesntExistException extends ApiException{
    public AttachmentDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Attachment doesn't exist!");
    }
    public AttachmentDoesntExistException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }
}
