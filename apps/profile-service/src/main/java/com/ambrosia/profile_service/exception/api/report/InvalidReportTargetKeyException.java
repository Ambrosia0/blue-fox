package com.ambrosia.profile_service.exception.api.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class InvalidReportTargetKeyException extends ApiException{
    public InvalidReportTargetKeyException(){
        super(HttpStatus.BAD_REQUEST, "Invalid report target key!");
    }
}
