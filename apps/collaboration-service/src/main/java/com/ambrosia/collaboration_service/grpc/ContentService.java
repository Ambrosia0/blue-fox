package com.ambrosia.collaboration_service.grpc;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.grpc.CollaborationContentRequest;
import com.ambrosia.content_service.grpc.CollaborationContentResponse;
import com.ambrosia.content_service.grpc.CollaborationSaveRequest;
import com.ambrosia.content_service.grpc.ContentServiceGrpc.ContentServiceBlockingStub;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class ContentService {
    private final ContentServiceBlockingStub contentService;

    public Optional<CollaborationContentResponse> getCollaborationContent(Long postId){
        try {
            var req = CollaborationContentRequest.newBuilder()
                .setPostId(postId)
                .build();
            return Optional.of(contentService.getCollaborationPost(req));
        } catch (StatusRuntimeException e) {
            return Optional.empty();
        }
    }

    public void saveCollaborationPost(Long postId, String content){
        try {
            var req = CollaborationSaveRequest.newBuilder()
                .setPostId(postId)
                .setContent(content)
                .build();
            contentService.saveCollaborationPost(req);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Not found!");
        }
    }
}
