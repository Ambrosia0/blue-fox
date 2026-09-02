package com.ambrosia.content_service.exception.api;

import org.springframework.http.HttpStatus;

public class AttachmentDoesntUploadedException extends ApiException{
    public AttachmentDoesntUploadedException(){
        super(HttpStatus.BAD_REQUEST, "Attachment doesnt uploaded by url!");
    }
}
