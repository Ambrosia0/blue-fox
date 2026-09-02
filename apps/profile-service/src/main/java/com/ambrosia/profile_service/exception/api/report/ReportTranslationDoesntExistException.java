package com.ambrosia.profile_service.exception.api.report;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class ReportTranslationDoesntExistException extends ApiException{
    public ReportTranslationDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Report translation doesn't exist!");
    }
}
