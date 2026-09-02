package com.ambrosia.report_service.exception.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.report_service.exception.ApiException;


public class ReportTargetDoesntExistException extends ApiException{
    public ReportTargetDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Report target doesn't exist!");
    }
}
