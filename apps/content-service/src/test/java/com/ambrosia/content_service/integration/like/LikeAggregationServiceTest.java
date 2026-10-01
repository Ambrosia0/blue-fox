package com.ambrosia.content_service.integration.like;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.like.service.LikeAggregationService;
import com.ambrosia.content_service.like.service.LikeUserService;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.UserCreator;

@Import({PostCreator.class, UserCreator.class})
public class LikeAggregationServiceTest extends BaseIntegrationTest{
    @Autowired LikeUserService likeUserService;
    @Autowired LikeAggregationService likeAggregationService;
    @Autowired PostLikeRepository postLikeRepository;

    @Autowired PostCreator postCreator;
    @Autowired UserCreator userCreator;

    @Test
    void aggregationTest() throws Exception{
        var firstPost = postCreator.create(true);
        var secondPost = postCreator.create(true);

        var deletable = userCreator.create().getId();
        var random = userCreator.create().getId();
        
        likeUserService.likePost(firstPost.getId(), deletable);
        likeUserService.likePost(firstPost.getId(), random);
        likeUserService.likePost(secondPost.getId(), random);
        likeUserService.unlikePost(firstPost.getId(), deletable);
        likeUserService.unlikePost(9999, random);
        likeUserService.likePost(9999, random);
        likeAggregationService.flush();
        assertEquals(2, postLikeRepository.count());
        likeUserService.unlikePost(firstPost.getId(), random);
        likeAggregationService.flush();
        assertEquals(1, postLikeRepository.count());
    }

    @AfterAll
    void cleanUp(){
        postLikeRepository.deleteAll();
        postCreator.cleanUp();
    }
}
