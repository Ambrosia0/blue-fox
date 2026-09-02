package com.ambrosia.content_service.search.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.utils.TextExtractor;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.repository.DocumentVectorRepository;
import com.ambrosia.content_service.search.repository.PostSearchRepository;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.PostSearchService;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

@Profile("es-disabled")
@AllArgsConstructor
@Service
public class DatabasePostIndexServiceImpl implements PostIndexService, PostSearchService{
    private final TextExtractor textExtractor;
    
    private final DocumentVectorRepository documentVectorRepository;

    private final PostSearchRepository postSearchRepository;

    @Transactional
    @Override
    public void index(Post post) {
        Assert.notNull(post.getId(), "Post id must not be null!");
        Assert.notNull(post.getContent(), "Content must not be null!");
        Assert.notNull(post.getTitle(), "Title must not be null!");
        var content = textExtractor.extractText(post.getContent());
        documentVectorRepository.insertDocument(
            post.getId(),
            content,
            post.getTags(),
            post.getTitle()
        );
    }

    @Transactional
    @Override
    public void reIndex(Post post) {
        Assert.notNull(post.getId(), "Post id must not be null!");
        Assert.notNull(post.getContent(), "Content must not be null!");
        var content = textExtractor.extractText(post.getContent());
        documentVectorRepository.update(
            post.getId(),
            content,
            post.getTags(),
            post.getTitle()
        );
    }

    // on delete cascade reference in database
    @Override
    public void deleteFromIndex(Long postId) {
        Assert.notNull(postId, "Post id must not be null!");
        documentVectorRepository.deleteById(postId);
    }

    @Override
    public List<PreviewWithScoreResponse> search(
            EventFilter eventFilter, 
            UUID requestingUser, 
            int pageSize,
            @Nullable List<UUID> blacklist
        ) {
        return postSearchRepository.search(eventFilter, requestingUser, pageSize, blacklist);
    }
}
