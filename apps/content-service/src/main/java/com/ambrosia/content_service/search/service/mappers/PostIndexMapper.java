package com.ambrosia.content_service.search.service.mappers;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.utils.TextExtractor;
import com.ambrosia.content_service.search.model.dto.PostIndex;
import com.ambrosia.content_service.search.model.entity.elastic.CommunityElastic;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;

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
                .visible(postIndex.post().isVisible())
                .reply(postIndex.post().getReplyId() != null? postIndex.post().getReplyId().getId(): null)
                .isNew(postIndex.post().isNew());
        if(postIndex.communityUserData() != null){
            builder.community(
                CommunityElastic.create(
                    postIndex.communityUserData().communityId(), 
                    postIndex.communityUserData().isCommunityPrivate()
                )
            );
        }
        return builder.build();
    }

    public PostIndex toIndex(
            Post post, 
            CommunityUserData communityUserData
    ){
        return PostIndex.builder()
            .communityUserData(communityUserData)
            .post(post)
            .build();
    }
}
