package com.ambrosia.report_service.exception.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.report_service.exception.ApiException;

public class ReportReasonDoesntExistException extends ApiException{
    public ReportReasonDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Report reason doesn't exist!");
    }
}
