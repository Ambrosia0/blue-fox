package com.ambrosia.report_service.exception.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.report_service.exception.ApiException;


public class ReportDoesntExistException extends ApiException{
    public ReportDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Report doesn't exist!");
    }
}
