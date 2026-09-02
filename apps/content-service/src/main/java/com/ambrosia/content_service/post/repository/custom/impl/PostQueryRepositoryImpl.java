package com.ambrosia.content_service.post.repository.custom.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.repository.custom.PostQueryRepository;
import com.ambrosia.content_service.post.repository.extractor.PostContentResponseMapper;
import com.ambrosia.content_service.post.repository.extractor.PostViewResponseMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PostQueryRepositoryImpl implements PostQueryRepository{
    private final JdbcClient jdbcClient;
    private PostContentResponseMapper postContentResponseMapper = new PostContentResponseMapper();
    private PostViewResponseMapper postViewResponseMapper = new PostViewResponseMapper();

    @Override
    public Optional<PostContentResponse> findPublishedByPostId(long postId) {
        var sql = """
        SELECT * FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        WHERE p.id = :postId AND p.published = 'true' AND p.visible = 'true'
        """;
        return jdbcClient
            .sql(sql)
            .param("postId", postId)
            .query(postContentResponseMapper)
            .optional();
    }

    @Override
    public List<PostViewResponse> findPreviewsByIdInList(List<Long> postIds) {
        var sql = """
        SELECT 
            p.id,
            p.author_id,
            p.title, 
            p.preview,
            p.tags,
            p.community_id,
            p.like_count,
            p.comment_count,
            p.view_count, 
            p.published_at,
            NULL::boolean as is_liked,
            cp.name,
            cp.avatar_id 
        FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        WHERE p.id IN (:ids)
        """;
        return jdbcClient
            .sql(sql)
            .param("ids", postIds)
            .query(postViewResponseMapper)
            .list();
    }

    @Override
    public List<PostViewResponse> findPreviewsByIdInListWithLike(List<Long> postIds, UUID requestingUser) {
        var sql = """
        SELECT 
            p.id,
            p.author_id,
            p.title, 
            p.preview,
            p.tags,
            p.community_id,
            p.like_count,
            p.comment_count,
            p.view_count,
            p.published_at,
            EXISTS(SELECT 1 FROM post_like pl WHERE pl.post_id = p.id AND pl.user_id = :requestingUser) as is_liked,
            cp.name,
            cp.avatar_id 
        FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        WHERE p.id IN (:ids)
        """;
        return jdbcClient
            .sql(sql)
            .param("ids", postIds)
            .param("requestingUser", requestingUser)
            .query(postViewResponseMapper)
            .list();
    }
}
