package com.ambrosia.content_service.post.service.mappers;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.entity.Post;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class PostMapper {
    private final PreviewConverter previewConverter;

    public Post apply(Post post, PostEditRequest postEditRequest){
        if(postEditRequest.post() != null){
            post.setPreview(previewConverter.convert(postEditRequest.post()));
            post.setContent(postEditRequest.post());
        }
        if(postEditRequest.title() != null)
            post.setTitle(postEditRequest.title());
        if(postEditRequest.tags() != null)
            post.setTags(postEditRequest.tags());
        
        post.setUpdatedAt(Instant.now());

        return post;
    }

    public Post toPublishedState(Post post){
        if(post.getPublishedAt() != null){
            post.setRepublished(true);
        }else{
            post.setPublishedAt(Instant.now());
        }
        post.setPublished(true);
        return post;
    }

    public Post toUnpublishedState(Post post){
        post.setPublished(false);
        return post;
    }
}
