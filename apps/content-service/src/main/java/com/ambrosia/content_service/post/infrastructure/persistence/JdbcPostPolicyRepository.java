package com.ambrosia.content_service.post.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.kafka_events.Permission;
import com.ambrosia.content_service.post.application.policy.PostPolicyRepository;
import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostCreatePolicyData;
import com.ambrosia.content_service.post.domain.policy.entity.PostDeletePolicyData;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.content_service.post.domain.policy.entity.PostViewPolicyData;
import com.ambrosia.content_service.post.infrastructure.persistence.extractor.PostCreatePolicyResultSetExtractor;
import com.ambrosia.content_service.post.infrastructure.persistence.mapper.CommunityDataRowMapper;
import com.ambrosia.content_service.post.infrastructure.persistence.mapper.PostPublishPolicyDataRowMapper;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcPostPolicyRepository implements PostPolicyRepository{
    private final JdbcClient jdbcClient;

    private final CommunityDataRowMapper communityDataRowMapper;

    private final PostCreatePolicyResultSetExtractor postCreatePolicyResultSetExtractor;

    private final PostPublishPolicyDataRowMapper postPublishPolicyDataRowMapper;

    @Override
    public Optional<CommunityData> loadCommunityData(UUID userId, Long communityId) {
        var sql ="""
        SELECT 
            cp.is_private as is_private,
            EXISTS(
                SELECT 1 FROM community_follow_projection cf
                WHERE cf.user_id = :userId
                AND cf.community_id = cp.id
            ) as is_followed,
            EXISTS(
                SELECT 1 FROM community_ban_projection cb
                WHERE cb.user_id = :userId
                AND cb.community_id = cp.id
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_permission cpr
                WHERE cpr.community_id = cp.id
                AND user_id = :userId
            ) as is_moderator
        FROM community_projection cp
        WHERE cp.id = :communityId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId")
            .param("communityId")
            .query(communityDataRowMapper)
            .optional();
    }

    @Override
    public PostCreatePolicyData loadForCreate(UUID userId, Long replyingPostId, @Nullable Long postedCommunityId) {
        var sql = """
        SELECT replying.*, posted.*
        FROM (
            SELECT
                cp.id as posted_community_id, 
                cp.is_private as posted_is_private,
                EXISTS(
                    SELECT 1 FROM community_follow_projection cf
                    WHERE cf.user_id = :userId
                    AND cf.community_id = cp.id
                ) as posted_is_followed,
                EXISTS(
                    SELECT 1 FROM community_ban_projection cb
                    WHERE cb.user_id = :userId
                    AND cb.community_id = cp.id
                ) as posted_is_banned
            FROM community_projection cp
            WHERE cp.id = :communityId
        ) posted

        FULL OUTER JOIN(
            SELECT 
                p.id as replying_post_id,
                cp.id as replying_community_id,
                cp.is_private as replying_is_private,
                EXISTS(
                    SELECT 1 FROM community_follow_projection cf
                    WHERE cf.user_id = :userId
                    AND cf.community_id = cp.id
                ) as replying_is_followed,
                EXISTS(
                    SELECT 1 FROM community_ban_projection cb
                    WHERE cb.user_id = :userId
                    AND cb.community_id = cp.id
                ) as replying_is_banned,
                EXISTS(
                    SELECT 1 FROM community_permission cpr
                    WHERE cpr.user_id = :userId
                    AND cpr.community_id = cp.id
                ) as replying_is_moderator
            FROM post p
            LEFT JOIN community_projection cp ON cp.id = p.community_id
            WHERE p.id = :postId
        ) replying ON TRUE
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("postId", replyingPostId)
            .param("communityId", postedCommunityId)
            .query(postCreatePolicyResultSetExtractor);
    }

    @Override
    public Optional<PostPublishPolicyData> loadForPublish(UUID userId, Long postId) {
        var sql ="""
        SELECT replying.*, posted.*
        FROM(
            SELECT
                p.author_id,
                p.reply_id,
                cp.id as community_id,
                EXISTS(
                    SELECT 1 FROM community_follow_projection cf
                    WHERE cf.user_id = :userId
                    AND cf.community_id = cp.id
                ) as is_followed,
                EXISTS(
                    SELECT 1 FROM community_ban_projection cb
                    WHERE cb.user_id = :userId
                    AND cb.community_id = cp.id
                ) as is_banned,
                EXISTS(
                    SELECT 1 FROM community_permission cpr
                    WHERE cpr.user_id = :userId
                    AND cpr.community_id = cp.id
                ) as is_moderator
            FROM post p
            LEFT JOIN community_projection cp ON cp.id = p.community_id
            WHERE p.id = :postId
        ) posted
        LEFT JOIN LATERAL(
            SELECT 
                cp.id as replying_community_id,
                cp.is_private as replying_is_private,
                EXISTS(
                    SELECT 1 FROM community_follow_projection cf
                    WHERE cf.user_id = :userId
                    AND cf.community_id = cp.id
                ) as replying_is_followed,
                EXISTS(
                    SELECT 1 FROM community_ban_projection cb
                    WHERE cb.user_id = :userId
                    AND cb.community_id = cp.id
                ) as replying_is_banned,
                EXISTS(
                    SELECT 1 FROM community_permission cpr
                    WHERE cpr.user_id = :userId
                    AND cpr.community_id = cp.id
                )
            FROM post p
            LEFT JOIN community_projection cp ON cp.id = p.community_id
            WHERE p.id = posted.reply_id
        ) replying ON TRUE
        """;
        return jdbcClient
            .sql(sql)
            .param("postId", postId)
            .param("userId", userId)
            .query(postPublishPolicyDataRowMapper)
            .optional();
            
    }

    @Override
    public Optional<PostDeletePolicyData> loadForDelete(UUID userId, Long postId) {
        var sql = """
        SELECT
            EXISTS(
                SELECT 1 FROM community_permission cpr
                WHERE cpr.community_id = p.community_id
                AND cpr.user_id = :userId
                AND cpr.permission = :permission
            ) as has_delete_permission,
            p.author_id
        FROM post p
        WHERE p.id = :postId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("postId", postId)
            .param("permission", Permission.POST_DELETE.name())
            .query(PostDeletePolicyData.class)
            .optional();
    }

    @Override
    public Optional<PostViewPolicyData> loadForView(UUID userId, Long postId) {
        var sql = """
        SELECT
            p.authorId, 
            cp.is_private,
            EXISTS(
                SELECT 1 FROM community_follow_projection cf
                WHERE cf.user_id = :userId
                AND cf.community_id = cp.id
            ) as is_followed,
            EXISTS(
                SELECT 1 FROM community_ban_projection cb
                WHERE cb.user_id = :userId
                AND cb.community_id = cp.id
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_permission cpr
                WHERE cpr.community_id = cp.id
                AND user_id = :userId
            ) as is_moderator
        FROM post p
        LEFT JOIN community_projection cp ON cp.id = p.community_id
        WHERE p.id = :postId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("postId", postId)
            .query((rs, rowNum) -> new PostViewPolicyData(
                rs.getObject("authorId", UUID.class),
                communityDataRowMapper.mapRow(rs, rowNum)
            ))
            .optional();
    }
}
