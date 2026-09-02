package com.ambrosia.profile_service.exception.api.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class ReportDoesntExistException extends ApiException{
    public ReportDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Report doesn't exist!");
    }
}
