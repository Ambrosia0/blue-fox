package com.ambrosia.comment_service.core.policy;

import java.util.UUID;

import com.ambrosia.comment_service.community.model.dto.CommentUserData;
import com.ambrosia.comment_service.community.model.dto.CommunityUserData;
import com.ambrosia.comment_service.community.utils.CommentPolicy;

public class AdminActor extends AbstractPolicy implements CommentPolicy{
    public AdminActor(UUID userId){
        super(userId);
    }

    @Override
    public void validateCommentCreate(CommunityUserData communityUserData) {
        return;        
    }

    @Override
    public void validateCommentLike(CommunityUserData communityUserData) {
        return;
    }

    @Override
    public void validateCommentView(CommunityUserData communityUserData) {
        return;
    }

    @Override
    public void validateDelete(CommentUserData commentUserData) {
        return;
    }
}
