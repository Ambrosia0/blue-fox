package com.ambrosia.content_service.like.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.like.service.LikeAggregationService;
import com.ambrosia.content_service.like.service.LikeUserService;
import com.ambrosia.content_service.post.exception.PostDoesntLikedException;
import com.ambrosia.content_service.post.exception.PostLikedException;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeUserServiceImpl implements LikeUserService{
    private final @Nullable LikeAggregationService likeAggregationService;

    private final PostLikeRepository postLikeRepository;

    @Override
    public void likePost(long postId, UUID userId) {
        if(likeAggregationService != null)
            likeAggregationService.add(postId, userId, true);
        else if(postLikeRepository.saveWithoutCheck(userId, postId) == 0){
            throw new PostLikedException();
        }

    }

    @Override
    public void unlikePost(long postId, UUID userId) {
        if(likeAggregationService != null)
            likeAggregationService.add(postId, userId, false);
        else if(postLikeRepository.returningDelete(userId, postId) == 0){
            throw new PostDoesntLikedException();
        }
    }

    @Override
    public boolean isLiked(long postId, UUID userId) {
        return postLikeRepository.existsById(userId, postId);
    }
}
