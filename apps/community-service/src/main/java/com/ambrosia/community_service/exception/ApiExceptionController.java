package com.ambrosia.community_service.exception;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(ApiException.class)
    public ErrorResponse forbidden(ApiException ex){
        return ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
    }
}
