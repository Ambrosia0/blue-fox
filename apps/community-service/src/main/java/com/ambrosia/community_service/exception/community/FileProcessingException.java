package com.ambrosia.community_service.exception.community;

import org.springframework.http.HttpStatus;

import com.ambrosia.community_service.exception.ApiException;

public class FileProcessingException extends ApiException{
    public FileProcessingException(){
        super(HttpStatus.BAD_REQUEST, "Can't process avatar file!");
    }
}
