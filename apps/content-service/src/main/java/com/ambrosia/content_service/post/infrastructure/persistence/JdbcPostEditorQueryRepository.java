package com.ambrosia.content_service.post.infrastructure.persistence;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.post.api.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.api.dto.response.PostCollaborationContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.application.query.PostEditorQueryRepository;
import com.ambrosia.content_service.post.infrastructure.persistence.mapper.PostCollaborationRowMapper;
import com.ambrosia.content_service.post.infrastructure.persistence.mapper.PostEditorContentResponseMapper;
import com.ambrosia.content_service.post.infrastructure.persistence.mapper.PostEditorViewResponseMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcPostEditorQueryRepository implements PostEditorQueryRepository{
    private final JdbcClient jdbcClient;

    private final PostCollaborationRowMapper postCollaborationRowMapper;
    private final PostEditorContentResponseMapper postEditorResponseMapper;
    private final PostEditorViewResponseMapper postEditorViewResponseMapper;

    @Override
    public Optional<PostEditorContentResponse> findUnpublishedByPostIdAndUserId(long postId, UUID userId) {
        var sql = """
        SELECT
            p.*,
            (
                SELECT array_agg(pa.id)
                FROM post_attachment pa
                WHERE pa.post_id = p.id
            ) as attachment_ids,
            cp.name,
            cp.slug,
            cp.is_private,
            cp.avatar_id
        FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        WHERE p.id = :postId 
        AND p.published = 'false' 
        AND p.author_id = :userId
        """;
        return jdbcClient
            .sql(sql)
            .param("postId", postId)
            .param("userId", userId)
            .query(postEditorResponseMapper)
            .optional();
    }

    @Override
    public Optional<PostCollaborationContentResponse> findCollaborationPostById(long postId) {
        var sql = """
        SELECT
            p.id,
            p.author_id,
            p.title,
            p.content,
            p.tags,
            p.community_id,
            p.updated_at,
            (
                SELECT array_agg(pa.id) 
                FROM post_attachment pa
                WHERE pa.post_id = p.id
            ) as attachment_ids,
            COALESCE(
                (
                    SELECT jsonb_agg(
                        jsonb_build_object(
                            'id', up.id, 
                            'username', up.username, 
                            'firstName', up.first_name, 
                            'lastName', up.last_name,
                            'avatarId', up.avatar_id
                        )
                    ) 
                    FROM post_collaboration pc
                    JOIN user_projection up ON up.id = pc.user_id
                    WHERE pc.post_id = p.id
                ), 
                '[]'::jsonb
            )::text as collaborators,
            up.*
        FROM post p
        JOIN user_projection up ON up.id = p.author_id
        WHERE p.id = :postId
        AND p.published = 'false'
        """;
        return jdbcClient
            .sql(sql)
            .param("postId", postId)
            .query(postCollaborationRowMapper)
            .optional();
    }
    
    @Override
    public Slice<PostEditorViewResponse> findUnpublishedPreviewsByUserId(UUID userId, PostEditorFilter filter, Pageable pageable) {
        var paramMap = new HashMap<String, Object>(7);
        var sql = new StringBuilder("""
        SELECT
            p.id,
            p.author_id,
            p.title,
            p.updated_at,
            p.tags,
            p.community_id,
            cp.name,
            cp.slug,
            cp.is_private,
            cp.avatar_id,
            COALESCE(
                (
                    SELECT jsonb_agg(
                        jsonb_build_object(
                            'id', up.id, 
                            'username', up.username, 
                            'firstName', up.first_name, 
                            'lastName', up.last_name,
                            'avatarId', up.avatar_id
                        )
                    ) 
                    FROM post_collaboration pc
                    JOIN user_projection up ON up.id = pc.user_id
                    WHERE pc.post_id = p.id
                ), 
                '[]'::jsonb
            )::text as collaborators,
            up.*
        FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        JOIN user_projection up ON up.id = p.author_id
        WHERE (
            p.author_id = :userId 
            OR 
            EXISTS(
                SELECT 1 FROM post_collaboration pc
                WHERE pc.post_id = p.id
                AND user_id = :userId
            )
        )
        AND p.published = 'false' 
        """);
        paramMap.put("userId", userId);

        if(filter.lastSeenDate() != null && filter.lastSeenId() != null){
            if(filter.direction() == Direction.DESC){
                sql.append("AND (p.updated_at, p.id) < (:lastSeenDate, :lastSeenId) ");
                sql.append("ORDER BY p.updated_at DESC, p.id DESC ");
            } else{
                sql.append("AND (p.updated_at, p.id) > (:lastSeenDate, :lastSeenId) ");
                sql.append("ORDER BY p.updated_at ASC, p.id ASC ");
            }
            paramMap.put("lastSeenDate", filter.lastSeenDate());
            paramMap.put("lastSeenId", filter.lastSeenId());

        }else{
            if(filter.direction() == Direction.DESC){
                sql.append("ORDER BY p.updated_at DESC, p.id DESC ");
            } else{
                sql.append("ORDER BY p.updated_at ASC, p.id ASC ");
            }
        }
        sql.append("LIMIT :limit");
        paramMap.put("limit", pageable.getPageSize() + 1);

        var res = jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query(postEditorViewResponseMapper)
            .list();
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext)
            res.remove(res.size());

        return new SliceImpl<>(res, pageable, hasNext);
    }

    @Override
    public Optional<PostEditorViewResponse> findUnpublishedViewByPostIdAndUserId(long postId, UUID userId) {
        var sql = """
        SELECT
            p.id,
            p.author_id,
            p.title,
            p.updated_at,
            p.tags,
            p.community_id,
            cp.name,
            cp.slug,
            cp.is_private,
            cp.avatar_id,
            COALESCE(
                (
                    SELECT jsonb_agg(
                        jsonb_build_object(
                            'id', up.id, 
                            'username', up.username, 
                            'firstName', up.first_name, 
                            'lastName', up.last_name,
                            'avatarId', up.avatar_id
                        )
                    ) 
                    FROM post_collaboration pc
                    JOIN user_projection up ON up.id = pc.user_id
                    WHERE pc.post_id = p.id
                ), 
                '[]'::jsonb
            )::text as collaborators,
            up.*
        FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        JOIN user_projection up ON up.id = p.author_id
        WHERE p.id = :postId
        AND (
            p.author_id = :userId 
            OR 
            EXISTS(
                SELECT 1 FROM post_collaboration pc
                WHERE pc.post_id = p.id
                AND pc.user_id = :userId
            )
        )
        AND p.published = 'false' 
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("postId", postId)
            .query(postEditorViewResponseMapper)
            .optional();
    }
}
