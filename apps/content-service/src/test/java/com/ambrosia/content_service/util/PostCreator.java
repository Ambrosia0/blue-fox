package com.ambrosia.content_service.util;


import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;

@TestComponent
public class PostCreator {
    @Autowired PostRepository postRepository;

    @Autowired CommunityProjectionRepository communityProjectionRepository;

    public Post createFromScratch(){
        return postRepository.save(
            PostFactory.createTestPost()
        );
    }

    public Post createUnpublished(){
        var post = PostFactory.createTestPost();
        post.setPublished(false);
        post.setPublishedAt(null);
        return postRepository.save(post);
    }

    public Post createPublished(){
        var post = PostFactory.createTestPost();
        post.setPublished(true);
        post.setPublishedAt(null);
        return postRepository.save(post);
    }

    public Post createPublishedWithPrivateCommunity(){
        var community = communityProjectionRepository.save(CommunityFactory.createPrivate());
        var post = PostFactory.createTestPost();
        post.setPublished(true);
        post.setPublishedAt(Instant.now());
        post.setCommunityId(AggregateReference.to(community.getId()));
        return postRepository.save(post);
    }
    public Post createPublishedWithPublicCommunity(){
        var community = communityProjectionRepository.save(CommunityFactory.createPublic());
        var post = PostFactory.createTestPost();
        post.setPublished(true);
        post.setPublishedAt(Instant.now());
        post.setCommunityId(AggregateReference.to(community.getId()));
        return postRepository.save(post);
    }
}
