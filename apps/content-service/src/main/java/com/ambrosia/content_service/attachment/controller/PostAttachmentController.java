package com.ambrosia.content_service.attachment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.attachment.model.dto.request.FileMetadata;
import com.ambrosia.content_service.attachment.model.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.model.entity.PostAttachment;
import com.ambrosia.content_service.attachment.service.PostAttachmentUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/api/me/post/{postId}")
@RestController
@RequiredArgsConstructor
@Validated
public class PostAttachmentController {
    private final PostAttachmentUserService attachmentService;
    
    @PostMapping(path = "/attachment")
    public AttachmentUploadResponse attachMedia(
        @PathVariable long postId,
        @RequestBody @Valid FileMetadata attachment,
        @AuthenticationPrincipal Jwt jwt) {
        return attachmentService.uploadAttachment(
            UUID.fromString(jwt.getSubject()),
            postId,
            attachment
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/attachment/{attachmentId}")
    public void confirmAttachment(
            @PathVariable long postId,
            @PathVariable String attachmentId,
            @AuthenticationPrincipal Jwt jwt) {
        attachmentService.validateAttachmentUpload(
            UUID.fromString(jwt.getSubject()),
            postId,
            attachmentId
        );
    }

    @GetMapping("/attachment")
    public List<PostAttachment> getAttachedMedia(
        @PathVariable long postId,
        @AuthenticationPrincipal Jwt jwt) {
        return attachmentService.getAttachments(
            UUID.fromString(jwt.getSubject()), 
            postId
        );
    }

    @DeleteMapping(path = "/attachment/{attachmentId}")
    public void deleteAttachment(
        @PathVariable long postId,
        @PathVariable String attachmentId,
        @AuthenticationPrincipal Jwt jwt){
        attachmentService.deleteAttachment(
            UUID.fromString(jwt.getSubject()),
            postId,
            attachmentId
        );
    }
}
