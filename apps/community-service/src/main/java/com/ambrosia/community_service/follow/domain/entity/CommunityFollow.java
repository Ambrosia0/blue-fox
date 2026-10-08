package com.ambrosia.community_service.follow.domain.entity;

import java.util.UUID;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor(
    onConstructor_ = @PersistenceCreator,
    access = AccessLevel.PACKAGE
)
public class CommunityFollow {
    private final UUID userId;

    private final Long communityId;

    private FollowState state;

    public void unfollow(){
        if(state == FollowState.FOLLOWED || state == FollowState.REQUESTED){
            state = FollowState.UNFOLLOWED;
        }
    }

    public enum FollowState{
        FOLLOWED,
        REQUESTED,
        UNFOLLOWED;
    }

    public static Builder buidler(){
        return new Builder();
    }

    public static class Builder{
        private UUID userId;

        private Long communityId;

        private FollowState state = FollowState.UNFOLLOWED;

        public Builder userId(UUID userId){
            this.userId = userId;
            return this;
        }

        public Builder communityId(Long communityId){
            this.communityId = communityId;
            return this;
        }

        public Builder requiresApproval(boolean requiresApproval){
            if(requiresApproval)
                this.state = FollowState.REQUESTED;
            else
                this.state = FollowState.FOLLOWED;
            return this;
        }
    
        public CommunityFollow build(){
            Assert.notNull(communityId, "Community id must not be null!");
            Assert.notNull(userId, "User id must not be null!");
            return new CommunityFollow(
                userId, 
                communityId, 
                state
            );
        }
    }
}
