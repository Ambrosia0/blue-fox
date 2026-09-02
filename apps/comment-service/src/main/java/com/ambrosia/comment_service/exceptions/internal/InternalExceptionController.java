package com.ambrosia.comment_service.exceptions.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InternalExceptionController {
    @ExceptionHandler(FileAttachmentException.class)
    public ErrorResponse fileAttachment(FileAttachmentException ex){
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Can't attach file!");
    }

    @ExceptionHandler(AttachmentDoesntExist.class)
    public ErrorResponse attachmentDoesntExist(AttachmentDoesntExist ex){
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, "Attachment doesn't exist");
    }
}
