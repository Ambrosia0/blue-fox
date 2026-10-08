package com.ambrosia.content_service.infrastructure.grpc.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.grpc.CollaborationContentResponse;
import com.ambrosia.content_service.grpc.User;
import com.ambrosia.content_service.post.api.dto.response.PostCollaborationContentResponse;
import com.ambrosia.content_service.post.api.dto.response.UserResponse;


@Component 
public class CollaborationContentMapper {
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
        if(response.attachmentIds() != null)
            builder.addAllAttachmentIds(response.attachmentIds());
        return builder.build();
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
