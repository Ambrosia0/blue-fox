package com.ambrosia.profile_service.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class FileProcessingException extends ApiException{
    public FileProcessingException(){
        super(HttpStatus.BAD_REQUEST, "Can't process avatar file!");
    }
}
