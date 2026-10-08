package com.ambrosia.content_service.infrastructure.grpc;

import org.springframework.grpc.server.service.GrpcService;

import com.ambrosia.content_service.core.PostValidator;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.grpc.CollaborationContentRequest;
import com.ambrosia.content_service.grpc.CollaborationContentResponse;
import com.ambrosia.content_service.grpc.CollaborationSaveRequest;
import com.ambrosia.content_service.grpc.CollaborationSaveResponse;
import com.ambrosia.content_service.grpc.ContentServiceGrpc.ContentServiceImplBase;
import com.ambrosia.content_service.infrastructure.grpc.mapper.CollaborationContentMapper;
import com.ambrosia.content_service.post.application.query.PostEditorQueryRepository;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.post.utils.ConvertedDoc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@GrpcService 
public class GrpcContentService extends ContentServiceImplBase{
    private final PostEditorQueryRepository postEditorQueryRepository;

    private final PostRepository postRepository;

    private final CollaborationContentMapper collaborationContentMapper;

    private final PreviewConverter previewConverter;

    private final PostValidator postValidator;

    @Override
    public void getCollaborationPost(CollaborationContentRequest request,
            StreamObserver<CollaborationContentResponse> responseObserver) {
        var post = postEditorQueryRepository.findCollaborationPostById(request.getPostId());
        if(post.isEmpty() || 
                post.get().collaborators() == null ||
                post.get().collaborators().isEmpty()
        ){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Post doesn't exist!")
                    .asRuntimeException()
            );
            return;
        }
        responseObserver.onNext(collaborationContentMapper.toResponse(post.get()));
        responseObserver.onCompleted();
    }

    @Override
    public void saveCollaborationPost(CollaborationSaveRequest request,
            StreamObserver<CollaborationSaveResponse> responseObserver) {
        var postOpt = postRepository.findById(request.getPostId());
        if(postOpt.isEmpty() || 
                postOpt.get().getCollaborationUsers() == null ||
                postOpt.get().getCollaborationUsers().isEmpty()
        ){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Post doesn't exist!")
                    .asRuntimeException()
            );
            return;
        }
        var post = postOpt.get();

        if(!postValidator.isValid(request.getContent())){
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Invalid post content!")
                    .asRuntimeException()
            );
            return;
        }

        post.edit(ConvertedDoc.from(
            request.getContent(), 
            previewConverter.convert(request.getContent()))
        );
        postRepository.save(post);
        responseObserver.onNext(CollaborationSaveResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
