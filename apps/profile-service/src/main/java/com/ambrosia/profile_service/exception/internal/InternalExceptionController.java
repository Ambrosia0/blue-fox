package com.ambrosia.profile_service.exception.internal;

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

    @ExceptionHandler(ContentServiceUnavailableException.class)
    public ErrorResponse contentService(ContentServiceUnavailableException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Unavailable!");
    }

    @ExceptionHandler(CommentServiceUnavailableException.class)
    public ErrorResponse contentService(CommentServiceUnavailableException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Unavailable!");
    }

    @ExceptionHandler(IdpServiceUnavailableException.class)
    public ErrorResponse idpService(IdpServiceUnavailableException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Unavailable!");
    }

    @ExceptionHandler(CommunityServiceUnavaiableException.class)
    public ErrorResponse communityService(CommunityServiceUnavaiableException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Unavalable!");
    }
}
