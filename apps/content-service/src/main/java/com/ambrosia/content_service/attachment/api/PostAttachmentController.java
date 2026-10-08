package com.ambrosia.content_service.attachment.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.attachment.api.dto.request.FileMetadata;
import com.ambrosia.content_service.attachment.api.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.application.PostAttachmentUserService;
import com.ambrosia.content_service.attachment.domain.entity.PostAttachment;
import com.ambrosia.library_policy.policy.Actor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/api/v1/me/post/{postId}/attachment")
@RestController
@RequiredArgsConstructor
@Validated
public class PostAttachmentController {
    private final PostAttachmentUserService attachmentService;
    
    @PostMapping
    public AttachmentUploadResponse attachMedia(
        @PathVariable long postId,
        @RequestBody @Valid FileMetadata attachment,
        Actor actor
    ) {
        return attachmentService.uploadAttachment(
            actor,
            postId,
            attachment
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{attachmentId}")
    public void confirmAttachment(
            @PathVariable long postId,
            @PathVariable String attachmentId,
            Actor actor
        ) {
        attachmentService.validateAttachmentUpload(
            actor,
            postId,
            attachmentId
        );
    }

    @GetMapping
    public List<PostAttachment> getAttachedMedia(
        @PathVariable long postId,
        Actor actor) {
        return attachmentService.getAttachments(
            actor, 
            postId
        );
    }

    @DeleteMapping(path = "/{attachmentId}")
    public void deleteAttachment(
        @PathVariable long postId,
        @PathVariable String attachmentId,
        Actor actor){
        attachmentService.deleteAttachment(
            actor,
            postId,
            attachmentId
        );
    }
}
