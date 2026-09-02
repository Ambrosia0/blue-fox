package com.ambrosia.content_service.exception.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InternalExceptionController {
    @ExceptionHandler(FileServiceAccessException.class)
    public ErrorResponse fileServiceAccess(FileServiceAccessException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Can't attach file!");
    }

    @ExceptionHandler(CantValidateAttachmentException.class)
    public ErrorResponse storageError(CantValidateAttachmentException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Can't validate attachment!");
    }
}
