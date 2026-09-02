package com.ambrosia.report_service.exception.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.report_service.exception.ApiException;

public class ReportTranslationDoesntExistException extends ApiException{
    public ReportTranslationDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Report translation doesn't exist!");
    }
}
