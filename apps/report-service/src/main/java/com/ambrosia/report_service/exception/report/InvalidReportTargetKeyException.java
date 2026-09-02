package com.ambrosia.report_service.exception.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.report_service.exception.ApiException;


public class InvalidReportTargetKeyException extends ApiException{
    public InvalidReportTargetKeyException(){
        super(HttpStatus.BAD_REQUEST, "Invalid report target key!");
    }
}
