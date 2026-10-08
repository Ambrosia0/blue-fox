package com.ambrosia.content_service.search.infrastructure.mappers;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.domain.entity.Post;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.content_service.post.infrastructure.entity.CommunityElastic;
import com.ambrosia.content_service.post.infrastructure.entity.PostElastic;
import com.ambrosia.content_service.post.utils.TextExtractor;
import com.ambrosia.content_service.search.infrastructure.dto.PostIndex;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class PostIndexMapper {
    private final TextExtractor textExtractor;

    public PostElastic toEntity(PostIndex postIndex){
        var builder = PostElastic.builder()
                .id(postIndex.post().getId())
                .esid(postIndex.post().getId().toString())
                .content(textExtractor.extractText(postIndex.post().getContent()))   
                .tags(postIndex.post().getTags())
                .authorId(postIndex.post().getAuthorId().toString())
                .likeCount(postIndex.post().getLikeCount())
                .title(postIndex.post().getTitle())
                .publishedAt(postIndex.post().getPublishedAt())
                .version(postIndex.post().getVersion())
                .reply(postIndex.post().getReplyId() != null? postIndex.post().getReplyId().getId(): null)
                .isNew(postIndex.post().isNew());
        if(postIndex.communityElastic() != null){
            builder.community(postIndex.communityElastic());
        }
        return builder.build();
    }

    public PostIndex toIndex(
            Post post, 
            PostPublishPolicyData policyData
    ){
        return PostIndex.builder()
            .communityElastic(policyData.postedCommunity()
                .map(t -> CommunityElastic.create(t.communityId(), t.isPrivate()))
                .orElse(null)
            )
            .post(post)
            .build();
    }
}
