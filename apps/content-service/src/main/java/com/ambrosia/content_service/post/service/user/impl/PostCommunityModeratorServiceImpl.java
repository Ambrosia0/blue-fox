package com.ambrosia.content_service.post.service.user.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.grpc.CommunityService;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostCommunityModeratorService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostCommunityModeratorServiceImpl implements PostCommunityModeratorService{
    private final PostRepository postRepository;
    private final CommunityService communityService;

    @CacheEvict(cacheNames = "posts", key = "#postId")
    @Override
    public void deletePost(UUID requestingUser, long postId) {
        var communityId = postRepository.findCommunityId(postId)
            .orElseThrow(() -> new NotEnoughPermissionsException());
        var isAllowed = communityService.isUserAllowed(requestingUser, "POST_DELETE", communityId);
        if(!isAllowed)
            throw new NotEnoughPermissionsException();
        postRepository.deleteById(postId);
    }
}
