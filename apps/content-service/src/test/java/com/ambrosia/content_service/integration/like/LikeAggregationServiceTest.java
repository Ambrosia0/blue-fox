package com.ambrosia.content_service.integration.like;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.like.service.LikeAggregationService;
import com.ambrosia.content_service.like.service.LikeUserService;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.utils.TipTapPreviewConverter;
import com.ambrosia.content_service.search.repository.elastic.ElasticPostRepository;
import com.ambrosia.content_service.util.Factory;
import com.ambrosia.content_service.util.PostTemplate;

import tools.jackson.databind.ObjectMapper;

public class LikeAggregationServiceTest extends BaseIntegrationTest{
    @Autowired PostRepository postRepository;
    @Autowired LikeUserService likeUserService;
    @Autowired LikeAggregationService likeAggregationService;
    @Autowired PostLikeRepository postLikeRepository;
    @Autowired ElasticPostRepository elasticPostRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private final PreviewConverter previewConverter = 
        new TipTapPreviewConverter(objectMapper, 50, 1);

    @Test
    void aggregationTest() throws Exception{
        var firstPost = createTestPost();
        var secondPost = createTestPost();

        var deletable = UUID.randomUUID();
        var random = UUID.randomUUID();
        
        likeUserService.likePost(firstPost.getId(), deletable);
        likeUserService.likePost(firstPost.getId(), random);
        likeUserService.likePost(secondPost.getId(), UUID.randomUUID());
        likeUserService.unlikePost(firstPost.getId(), deletable);
        likeUserService.unlikePost(9999, UUID.randomUUID());
        likeUserService.likePost(9999, UUID.randomUUID());
        likeAggregationService.flush();
        assertEquals(2, postLikeRepository.count());
        likeUserService.unlikePost(firstPost.getId(), random);
        likeAggregationService.flush();
        assertEquals(1, postLikeRepository.count());
    }

    private Post createTestPost(){
        var post = Factory.createTestPost();
        post.setPreview(previewConverter.convert(PostTemplate.template));
        return postRepository.save(post);
    }

    @AfterAll
    void cleanUp(){
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        elasticPostRepository.deleteAll();
    }
}
