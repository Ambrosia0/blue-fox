package com.ambrosia.comment_service.community.utils;

import java.util.UUID;

import com.ambrosia.comment_service.community.model.dto.CommentUserData;
import com.ambrosia.comment_service.community.model.dto.CommunityUserData;

public interface CommentPolicy {
    void validateCommentView(CommunityUserData communityUserData);
    void validateCommentCreate(CommunityUserData communityUserData);
    void validateCommentLike(CommunityUserData communityUserData);
    void validateDelete(CommentUserData commentUserData);
    UUID id();
}
