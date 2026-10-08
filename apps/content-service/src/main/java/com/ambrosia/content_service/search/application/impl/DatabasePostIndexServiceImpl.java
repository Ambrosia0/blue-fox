package com.ambrosia.content_service.search.application.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.domain.repository.DocumentVectorRepository;
import com.ambrosia.content_service.post.domain.repository.PostSearchRepository;
import com.ambrosia.content_service.post.utils.TextExtractor;
import com.ambrosia.content_service.search.application.PostSearchService;
import com.ambrosia.content_service.search.infrastructure.PostIndexService;
import com.ambrosia.content_service.search.infrastructure.dto.PostIndex;

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
    public void index(PostIndex postIndex) {
        Assert.notNull(postIndex.post().getId(), "Post id must not be null!");
        Assert.notNull(postIndex.post().getContent(), "Content must not be null!");
        Assert.notNull(postIndex.post().getTitle(), "Title must not be null!");
        var content = textExtractor.extractText(postIndex.post().getContent());
        documentVectorRepository.insertDocument(
            postIndex.post().getId(),
            content,
            postIndex.post().getTags()
                .stream()
                .collect(Collectors.joining(",")),
            postIndex.post().getTitle()
        );
    }

    @Transactional
    @Override
    public void reIndex(PostIndex postIndex) {
        Assert.notNull(postIndex.post().getId(), "Post id must not be null!");
        Assert.notNull(postIndex.post().getContent(), "Content must not be null!");
        var content = textExtractor.extractText(postIndex.post().getContent());
        documentVectorRepository.update(
            postIndex.post().getId(),
            content,
            postIndex.post().getTags()
                .stream()
                .collect(Collectors.joining(",")),
            postIndex.post().getTitle()
        );
    }

    @Override
    public void deleteFromIndex(Long postId, Long version) {
        Assert.notNull(postId, "Post id must not be null!");
        Assert.notNull(postId, "Post version must not be null!");
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
