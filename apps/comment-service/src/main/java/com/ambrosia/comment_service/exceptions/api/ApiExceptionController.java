package com.ambrosia.comment_service.exceptions.api;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(ApiException.class)
    public ErrorResponse apiException(ApiException ex){
        return ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
    }

    // @ExceptionHandler()
    // public ErrorResponse dataIntegrity(DataIntegrityViolationException ex){
    //     return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "");
    // }
}
