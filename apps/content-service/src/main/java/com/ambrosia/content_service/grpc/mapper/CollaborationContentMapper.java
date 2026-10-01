package com.ambrosia.content_service.grpc.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.grpc.CollaborationContentResponse;
import com.ambrosia.content_service.grpc.CollaborationSaveRequest;
import com.ambrosia.content_service.grpc.User;
import com.ambrosia.content_service.post.model.dto.response.PostCollaborationContentResponse;
import com.ambrosia.content_service.post.model.dto.response.UserResponse;
import com.ambrosia.content_service.post.model.entity.Post;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CollaborationContentMapper {
    private final PreviewConverter previewConverter;
    
    public CollaborationContentResponse toResponse(
            PostCollaborationContentResponse response){
        var builder = CollaborationContentResponse.newBuilder()
            .setPostId(response.id())
            .setContent(response.content())
            .setAuthor(toUser(response.author()))
            .setLastUpdate(response.updatedAt().toEpochMilli());
        if(response.collaborators() != null)
            builder.addAllCollaborationUsers(response.collaborators()
                .stream()
                .map(this::toUser)
                .toList()
            );
        return builder.build();
    }

    public Post apply(Post post, CollaborationSaveRequest request){
        var preview = previewConverter.convert(request.getContent());
        post.setPreview(preview);
        post.setContent(request.getContent());
        post.setUpdatedAt(Instant.now());
        return post;
    }

    private User toUser(UserResponse userResponse){
        var builder = User.newBuilder()
            .setId(userResponse.id().toString())
            .setUsername(userResponse.username())
            .setFirstName(userResponse.firstName())
            .setLastName(userResponse.lastName());
        if(userResponse.avatarId() != null)
            builder.setAvatarId(userResponse.avatarId());
        return builder.build();
    }
}
