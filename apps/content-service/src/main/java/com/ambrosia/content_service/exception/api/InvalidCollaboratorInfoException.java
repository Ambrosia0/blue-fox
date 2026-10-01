package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class InvalidCollaboratorInfoException extends ApiException{
    public InvalidCollaboratorInfoException(){
        super(HttpStatus.BAD_REQUEST, "Invalid collaborator user information!");
    }
}
