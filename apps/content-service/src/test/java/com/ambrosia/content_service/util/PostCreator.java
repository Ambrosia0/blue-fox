package com.ambrosia.content_service.util;


import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.search.repository.DocumentVectorRepository;
import com.ambrosia.content_service.search.repository.elastic.ElasticPostRepository;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.mappers.PostIndexMapper;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;

@TestComponent
@Import({CommunityCreator.class, UserCreator.class})
public class PostCreator {
    @Autowired PostRepository postRepository;

    @Autowired PostIndexService postIndexService;

    @Autowired PostIndexMapper postIndexMapper;

    @Autowired CommunityCreator communityCreator;

    @Autowired UserCreator userCreator;

    @Autowired(required = false) DocumentVectorRepository documentVectorRepository;

    @Autowired(required = false) ElasticPostRepository elasticPostRepository;

    @Autowired(required = false) ElasticsearchOutboxRelay relay;

    @Autowired PreviewConverter previewConverter;

    public Post create(boolean isPublished){
        var user = userCreator.create();
        var post = createTestPost(user.getId());
        if(isPublished){
            post.setPublished(isPublished);
            post.setPublishedAt(Instant.now());
            post = postRepository.save(post);
            postIndexService.index(postIndexMapper.toIndex(post, null));
        }else{
            post.setPublished(isPublished);
            post.setPublishedAt(null);
            post = postRepository.save(post);
        }
        if(relay != null) relay.flush();
        return post;
    }

    public PostWithCommunity createPublishedWithCommunity(boolean isCommunityPrivate){
        var community = communityCreator.create(isCommunityPrivate);
        var user = userCreator.create();
        var post = createTestPost(user.getId());
        post.setPublished(true);
        post.setPublishedAt(Instant.now());
        post.setCommunityId(AggregateReference.to(community.getId()));
        post = postRepository.save(post);
        postIndexService.index(postIndexMapper.toIndex(post, null));
        if(relay != null) relay.flush();
        return new PostWithCommunity(
            post, 
            community
        );
    }

    private static Post createTestPost(UUID userId) {
        return Post.builder()
            .authorId(userId)
            .content(PostTemplate.template)
            .isNew(true)
            .title("Test Title" + ThreadLocalRandom.current().nextLong())
            .build();
    }

    public void cleanUp(){
        postRepository.deleteAll();
        if(documentVectorRepository != null)
            documentVectorRepository.deleteAll();
        if(elasticPostRepository != null)
            elasticPostRepository.deleteAll();
    }

    public record PostWithCommunity(
        Post post,
        CommunityProjection communityProjection
    ){}
}
