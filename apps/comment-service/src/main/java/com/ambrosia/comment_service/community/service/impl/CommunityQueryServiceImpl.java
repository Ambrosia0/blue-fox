package com.ambrosia.comment_service.community.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.community.model.dto.CommunityUserData;
import com.ambrosia.comment_service.community.repository.CommunityQueryRepository;
import com.ambrosia.comment_service.community.service.CommunityQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityQueryServiceImpl implements CommunityQueryService{
    private final CommunityQueryRepository communityQueryRepository;

    @Override
    public Optional<CommunityUserData> findCommunityUserDataByPostId(long postId, UUID userId) {
        return communityQueryRepository.findCommunityUserDataByPostId(postId, userId);
    }

    @Override
    public Optional<CommunityUserData> findCommunityUserDataByCommentId(long commentId, UUID userId) {
        return communityQueryRepository.findCommunityUserDataByCommentId(commentId, userId);
    }
}
