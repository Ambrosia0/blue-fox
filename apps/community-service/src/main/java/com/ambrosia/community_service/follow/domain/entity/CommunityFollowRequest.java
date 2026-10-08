package com.ambrosia.community_service.follow.domain.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.PersistenceCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter 
@AllArgsConstructor(
    onConstructor_ = @PersistenceCreator,
    access = AccessLevel.PACKAGE
)
public class CommunityFollowRequest{
    private final UUID userId;

    private final Long communityId;

    private final Instant createdAt;

    private FollowState state;


    public void accept(){
        state = FollowState.ACCEPTED;
    };

    public void decline(){
        state = FollowState.DECLINED;
    };

    public enum FollowState{
        DECLINED,
        ACCEPTED;
    }
}
