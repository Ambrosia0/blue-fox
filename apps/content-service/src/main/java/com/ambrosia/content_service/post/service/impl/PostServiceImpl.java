package com.ambrosia.content_service.post.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    
    @Override
    public boolean isAuthor(long postId, UUID userId) {
        return postRepository.existsByIdAndAuthorId(postId, userId);
    }

    @Override
    public boolean isExists(long postId) {
        return postRepository.existsByIdAndPublishedIsTrue(postId);
    }
}
