package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class FileProcessingException extends ApiException{
    public FileProcessingException(){
        super(HttpStatus.BAD_REQUEST, "Can't process file!");
    }
}
