package com.ambrosia.content_service.util;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.post.domain.entity.Post;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.content_service.post.domain.repository.DocumentVectorRepository;
import com.ambrosia.content_service.post.infrastructure.persistence.JdbcPostRepository;
import com.ambrosia.content_service.post.utils.ConvertedDoc;
import com.ambrosia.content_service.search.infrastructure.PostIndexService;
import com.ambrosia.content_service.search.infrastructure.elastic.ElasticPostRepository;
import com.ambrosia.content_service.search.infrastructure.mappers.PostIndexMapper;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;

@TestComponent
@Import({CommunityCreator.class, UserCreator.class})
public class PostCreator {
    @Autowired JdbcPostRepository postRepository;

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
        var post = createTestPost(user.getId(), null);
        post.edit(new ConvertedDoc(PostTemplate.template, previewConverter.convert(PostTemplate.template)));
        if(isPublished){
            post.publish();
            post = postRepository.save(post);
            postIndexService.index(
                postIndexMapper.toIndex(
                    post, 
                    new PostPublishPolicyData(null, Optional.empty(), Optional.empty())
                )
            );
        }else{
            post = postRepository.save(post);
        }
        if(relay != null) relay.flush();
        return post;
    }

    public PostWithCommunity createPublishedWithCommunity(boolean isCommunityPrivate){
        var community = communityCreator.create(isCommunityPrivate);
        var user = userCreator.create();
        var post = createTestPost(user.getId(), community.getId());
        
        post.edit(new ConvertedDoc(PostTemplate.template, previewConverter.convert(PostTemplate.template)));
        post.publish();
        post = postRepository.save(post);
        
        postIndexService.index(
            postIndexMapper.toIndex(
                post, 
                new PostPublishPolicyData(null, Optional.empty(), Optional.empty())
            )
        );
        if(relay != null) relay.flush();
        return new PostWithCommunity(
            post, 
            community
        );
    }

    public Post createCollaborationPost(boolean isPublished){
        var user = userCreator.create();
        var collabUsers = List.of(
            userCreator.create(),
            userCreator.create()
        );
        var post = createTestPost(user.getId(), null);
        post.setCollaborationUsers(collabUsers.stream()
                .map(t -> t.getId())
                .collect(Collectors.toSet())
        );
        
        post.edit(new ConvertedDoc(
            PostTemplate.template, 
            previewConverter.convert(PostTemplate.template))
        );

        if(isPublished){
            post.publish();
            post = postRepository.save(post);
            postIndexService.index(
                postIndexMapper.toIndex(
                    post, 
                    new PostPublishPolicyData(null, Optional.empty(), Optional.empty())
                )
            );
            if(relay != null) relay.flush();
            return post;
        }else{
            return postRepository.save(post);
        }
    }

    private static Post createTestPost(UUID userId, Long communityId) {
        return Post.builder()
            .authorId(userId)
            .communityId(communityId)
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
