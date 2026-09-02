package com.ambrosia.content_service.exception.api;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(ApiException.class)
    public ErrorResponse doesntFollowed(ApiException ex){
        return ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
    }

}
