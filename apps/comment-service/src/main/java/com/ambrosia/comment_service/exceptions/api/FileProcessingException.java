package com.ambrosia.comment_service.exceptions.api;

import org.springframework.http.HttpStatus;

public class FileProcessingException extends ApiException{
    public FileProcessingException(){
        super(HttpStatus.BAD_REQUEST, "Can't process file!");
    }
}
