package com.ambrosia.profile_service.exception.api.user;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

public class AvatarDoesntUploadedException extends ApiException{
    public AvatarDoesntUploadedException(){
        super(HttpStatus.BAD_REQUEST, "Avatar doesn't uploaded by url!");
    }
}
