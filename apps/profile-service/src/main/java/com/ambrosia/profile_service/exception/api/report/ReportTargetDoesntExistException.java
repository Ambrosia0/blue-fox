package com.ambrosia.profile_service.exception.api.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class ReportTargetDoesntExistException extends ApiException{
    public ReportTargetDoesntExistException(){
        super(HttpStatus.NOT_FOUND, "Report target doesn't exist!");
    }
}
