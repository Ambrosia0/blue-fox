package com.ambrosia.content_service.grpc;

import org.springframework.grpc.server.service.GrpcService;

import com.ambrosia.content_service.grpc.ContentServiceGrpc.ContentServiceImplBase;
import com.ambrosia.content_service.grpc.mapper.CollaborationContentMapper;
import com.ambrosia.content_service.post.repository.PostEditorQueryRepository;
import com.ambrosia.content_service.post.repository.PostRepository;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@GrpcService 
public class GrpcContentService extends ContentServiceImplBase{
    private final PostEditorQueryRepository postEditorQueryRepository;

    private final PostRepository postRepository;

    private final CollaborationContentMapper collaborationContentMapper;

    @Override
    public void getCollaborationPost(CollaborationContentRequest request,
            StreamObserver<CollaborationContentResponse> responseObserver) {
        var post = postEditorQueryRepository.findCollaborationPostById(request.getPostId());
        if(post.isEmpty()){
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
        var post = postRepository.findById(request.getPostId());
        if(post.isEmpty()){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Post doesn't exist!")
                    .asRuntimeException()
            );
            return;
        }
        postRepository.save(collaborationContentMapper.apply(post.get(), request));
    }
}
