package com.ambrosia.community_service.exception.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InternalExceptionController {
    @ExceptionHandler(FileAttachmentException.class)
    public ErrorResponse fileAttachment(FileAttachmentException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Can't process file!");
    }

    @ExceptionHandler
    public ErrorResponse storageException(StorageUnavailableException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Retry to upload later!");
    }
}
