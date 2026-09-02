package com.ambrosia.report_service.exception.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.report_service.exception.ApiException;


public class ReportAlreadyClosedException extends ApiException{
    public ReportAlreadyClosedException(){
        super(HttpStatus.BAD_REQUEST, "Report already closed!");
    }
}
