package com.ambrosia.profile_service.exception.api.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class ReportReasonDoesntExistException extends ApiException{
    public ReportReasonDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Report reason doesn't exist!");
    }
}
