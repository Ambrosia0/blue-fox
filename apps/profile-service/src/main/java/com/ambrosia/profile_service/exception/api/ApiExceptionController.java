package com.ambrosia.profile_service.exception.api;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ambrosia.profile_service.exception.ApiException;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(ApiException.class)
    public ErrorResponse forbidden(ApiException ex){
        return ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
    }
}
