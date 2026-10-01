package com.ambrosia.comment_service.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.post.model.entity.PostProjection;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;

@TestComponent
@Import({CommunityCreator.class})
public class PostProjectionCreator {
    @Autowired PostProjectionRepository postProjectionRepository;

    @Autowired CommunityCreator communityCreator;

    public PostProjection create(Long communityId){
        return postProjectionRepository.save(PostProjection.builder()
            .communityId(communityId)
            .id(ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .isPublished(true)
            .isNew(true)
            .build()
        );
    }
    
    public PostProjection create(){
        return postProjectionRepository.save(PostProjection.builder()
            .id(ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .isPublished(true)
            .isNew(true)
            .build()
        );
    }

    public PostWithCommunity createWithCommunity(boolean isCommunityPrivate){
        var community = communityCreator.create(isCommunityPrivate);
        return new PostWithCommunity(
            postProjectionRepository.save(PostProjection.builder()
                .id(ThreadLocalRandom.current().nextLong())
                .isNew(true)
                .isPublished(true)
                .communityId(community.getId())
                .build()
            ), 
            community
        );
    }

    public void cleanUp(){
        postProjectionRepository.deleteAll();
    }

    public record PostWithCommunity(
        PostProjection postProjection,
        CommunityProjection communityProjection
    ){}
}
