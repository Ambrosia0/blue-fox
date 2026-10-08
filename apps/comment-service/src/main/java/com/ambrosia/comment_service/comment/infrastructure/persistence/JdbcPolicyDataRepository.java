package com.ambrosia.comment_service.comment.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.comment.application.policy.PolicyDataRepository;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.DeletePolicyData;
import com.ambrosia.comment_service.comment.infrastructure.persistence.extractor.CommentPolicyRowMapper;
import com.ambrosia.comment_service.comment.infrastructure.persistence.extractor.DeletePolicyDataRowMapper;
import com.ambrosia.community_service.kafka_events.Permission;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcPolicyDataRepository implements PolicyDataRepository{
    private final JdbcClient jdbcClient;

    private final CommentPolicyRowMapper commentPolicyRowMapper;

    private final DeletePolicyDataRowMapper deletePolicyDataRowMapper;
    
    @Override
    public Optional<CommentPolicyData> loadByComment(UUID userId, Long commentId) {
        var sql = """
        SELECT 
            pp.community_id,
            EXISTS(
                SELECT 1 FROM community_ban_projection cbp
                WHERE cbp.community_id = pp.community_id
                AND cbp.user_id = :userId
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_permission cpr
                WHERE cpr.community_id = pp.community_id
                AND cpr.user_id = :userId
                AND permission = :permission
            ) as is_moderator,
            EXISTS(
                SELECT 1 FROM community_follow_projection cfp
                WHERE cfp.community_id = pp.community_id
                AND cfp.user_id = :userId
            ) as is_followed,
            COALESCE(cp.is_private, false)
        FROM comment c
        JOIN post_projection pp ON pp.post_id = c.post_id
        LEFT JOIN community_projection cp ON cp.id = pp.community_id
        WHERE pp.is_published = 'true'
        AND c.id = :commentId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("commentId", commentId)
            .param("permission", Permission.COMMENT_DELETE.name())
            .query(commentPolicyRowMapper)
            .optional();
    }

    @Override
    public Optional<CommentPolicyData> loadByPost(UUID userId, Long postId) {
        var sql = """
        SELECT 
            pp.community_id,
            EXISTS(
                SELECT 1 FROM community_ban_projection cbp
                WHERE cbp.community_id = pp.community_id
                AND cbp.user_id = :userId
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_permission cpr
                WHERE cpr.community_id = pp.community_id
                AND cpr.user_id = :userId
                AND permission = :permission
            ) as is_moderator,
            EXISTS(
                SELECT 1 FROM community_follow_projection cfp
                WHERE cfp.community_id = pp.community_id
                AND cfp.user_id = :userId
            ) as is_followed,
            COALESCE(cp.is_private, false)
        FROM post_projection pp
        LEFT JOIN community_projection cp ON cp.id = pp.community_id
        WHERE pp.is_published = 'true'
        AND pp.post_id = :postId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("postId", postId)
            .param("permission", Permission.COMMENT_DELETE.name())
            .query(commentPolicyRowMapper)
            .optional();
    }

    @Override
    public Optional<DeletePolicyData> loadDeleteData(UUID userId, Long commentId) {
        var sql ="""
        SELECT 
            EXISTS(
                SELECT 1 FROM community_permission cpr
                WHERE cpr.community_id = pp.community_id
                AND cpr.user_id = :userId
                AND permission = :permission
            ) as is_moderator,
            c.user_id
        FROM comment c
        JOIN post_projection pp ON pp.post_id = c.post_id
        LEFT JOIN community_projection cp ON cp.id = pp.community_id
        WHERE pp.is_published = 'true'
        AND c.is_visible = 'true'
        AND c.id = :commentId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("commentId", commentId)
            .param("permission", Permission.COMMENT_DELETE.name())
            .query(deletePolicyDataRowMapper)
            .optional();
    }
}
