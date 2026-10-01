package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import org.springframework.util.Assert;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.model.dto.PostUserData;

public class AdminActor extends AbstractPolicy implements PostPolicy{

    public AdminActor(UUID userId){
        Assert.notNull(userId, "User id must not be null!");
        super(userId);
    }

    @Override
    public void validateCreate(CommunityUserData userData) {}

    @Override
    public void validateReply(CommunityUserData userData) {}

    @Override
    public void validateView(CommunityUserData userData) {}

    @Override
    public void validateDelete(PostUserData userData) {}
}
