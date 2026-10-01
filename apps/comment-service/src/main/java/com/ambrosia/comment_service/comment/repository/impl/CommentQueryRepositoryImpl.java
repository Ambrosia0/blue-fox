package com.ambrosia.comment_service.comment.repository.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.EventFilter.SortField;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.comment.repository.CommentQueryRepository;
import com.ambrosia.comment_service.comment.repository.extractor.CommentDataMapper;
import com.ambrosia.comment_service.comment.repository.extractor.ScoredCommentDataMapper;
import com.ambrosia.comment_service.core.AppConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor 
@Slf4j
@Repository
public class CommentQueryRepositoryImpl implements CommentQueryRepository {
    private final JdbcClient jdbcClient;
    
    private final AppConfiguration appConfiguration;

    private final ScoredCommentDataMapper scoredCommentDataMapper;

    private final CommentDataMapper commentDataMapper;

    private static Map<SortField, String> columnMap = Map.of(
        SortField.DATE, "c.created_at",
        SortField.LIKES, "c.like_count"
    );

    @Override
    public List<ScoredCommentData> getRootCommentsForPost(long postId, UUID userId, EventFilter eventFilter, int pageSize) {
        var paramMap = new LinkedHashMap<String, Object>();
        var builder = switch(eventFilter.sortField()){
            case DATE, LIKES -> buildRootRequest(eventFilter, paramMap);
            case HOT -> buildRootHotRequest(eventFilter, paramMap);
            default -> buildRootHotRequest(eventFilter, paramMap);
        };
        paramMap.put("userId", userId);
        paramMap.put("postId", postId);
        
        builder.append(" LIMIT :pageSize");
        paramMap.put("pageSize", pageSize);

        return jdbcClient
            .sql(builder.toString())
            .params(paramMap)
            .query(scoredCommentDataMapper)
            .list();
    }

    @Override
    public List<ScoredCommentData> getTreeForPostComment(long commentId, UUID userId) {
        var query = 
        """
        WITH RECURSIVE comment_tree AS ( 
            SELECT 
                c.*
            FROM comment c
            JOIN post_projection pp ON pp.post_id = c.post_id
            WHERE c.id = :commentId
            AND pp.is_published = 'true'

            UNION ALL

            SELECT 
                child.*
            FROM comment child
            JOIN comment_tree ct ON child.parent_comment_id = ct.id
        )
        SELECT
            ct.*,
            ca.attachment_id,
            EXISTS(
                SELECT 1 FROM comment_like cl 
                WHERE cl.comment_id = ct.id 
                AND cl.user_id = :userId
            ) as is_liked,
            CASE 
                WHEN ct.parent_comment_id IS NOT NULL THEN(
                    (log(1 + ct.like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - ct.created_at)) + 1)))
                )
            END AS hot_score,
            up.username,
            up.first_name,
            up.last_name,
            up.avatar_id
        FROM comment_tree ct
        LEFT JOIN comment_attachment ca ON ca.comment_id = ct.id
        LEFT JOIN user_projection up ON up.id = ct.user_id
        WHERE ct.id != :commentId
        ORDER BY ct.parent_comment_id NULLS FIRST, ct.created_at ASC
        """;
        return jdbcClient
            .sql(query)
            .param("commentId", commentId)
            .param("userId", userId)
            .param("coeff", appConfiguration.getTimeAffectionCoefficient())
            .query(scoredCommentDataMapper)
            .list();
    }

    @Override
    public Optional<CommentData> getComment(long commentId, UUID userId) {
        var sql = """
            SELECT 
                c.*,
                ca.attachment_id,
                EXISTS(SELECT 1 FROM comment_like WHERE comment_id = :commentId AND user_id = :userId) as is_liked,
                up.username,
                up.first_name,
                up.last_name,
                up.avatar_id
            FROM comment c
            JOIN post_projection pp ON pp.post_id = c.post_id
            LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
            LEFT JOIN user_projection up ON up.id = c.user_id
            WHERE c.id = :commentId
            AND pp.is_published = 'true'
            """;
        return jdbcClient
            .sql(sql)
            .param("commentId", commentId)
            .param("userId", userId)
            .query(commentDataMapper)
            .optional();
    }

    private StringBuilder buildRootHotRequest(EventFilter eventFilter, LinkedHashMap<String, Object> paramMap){
        var buider = new StringBuilder(
        """
        SELECT 
            c.*,
            ca.attachment_id, 
            (log(1 + like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - created_at)) + 1))) AS hot_score, 
            EXISTS(SELECT 1 FROM comment_like cl WHERE cl.comment_id = c.id AND cl.user_id = :userId) as is_liked,
            up.username,
            up.first_name,
            up.last_name,
            up.avatar_id
        FROM comment c
        LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
        LEFT JOIN user_projection up ON up.id = c.user_id
        JOIN post_projection pp ON pp.post_id = c.post_id
        WHERE c.post_id = :postId
        AND pp.is_published = 'true'
        AND parent_comment_id IS NULL 
        """);

        paramMap.put("coeff", appConfiguration.getTimeAffectionCoefficient());
        if(eventFilter.lastSeenId() != null){
            buider.append("AND id < :lastId ");
            paramMap.put("lastId", eventFilter.lastSeenId());
        }
        buider.append("ORDER BY hot_score DESC, c.id DESC ");
        return buider;
    }

    private StringBuilder buildRootRequest(EventFilter eventFilter, LinkedHashMap<String, Object> paramMap){
        var builder = new StringBuilder(
        """
        SELECT 
            c.*,
            ca.attachment_id,
            EXISTS(SELECT 1 FROM comment_like WHERE comment_id = c.id AND user_id = :userId) as is_liked 
            up.username,
            up.first_name,
            up.last_name,
            up.avatar_id
        FROM comment c 
        LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
        LEFT JOIN user_projection up ON up.id = c.user_id
        JOIN post_projection pp ON pp.post_id = c.id
        WHERE c.post_id = :postId
        AND pp.is_published = 'true' 
        AND c.parent_comment_id IS NULL 
        """);
        String col = eventFilter.sortField() != null? columnMap.get(eventFilter.sortField()): "c.like_count";
        var dir = eventFilter.direction() != null? eventFilter.direction(): Direction.DESC;

        Object cursor1 = null;
        Object cursor2 = null;
        var hasCursor = false;
        String operation;

        if(eventFilter.lastSeenCount() != null && eventFilter.lastSeenId() != null){
            cursor1 = eventFilter.lastSeenCount();
            cursor2 = eventFilter.lastSeenId();
            hasCursor = true;

        }else if(eventFilter.lastSeenInstant() != null && eventFilter.lastSeenId() != null){
            cursor1 = eventFilter.lastSeenInstant();
            cursor2 = eventFilter.lastSeenId();
            hasCursor = true;
        }
        if(dir == Direction.DESC){
            operation = hasCursor? "<": null;
        }else{
            operation = hasCursor? ">": null;
        }

        if(hasCursor){
            builder
                .append("AND (")
                .append(col)
                .append(", c.id) ")
                .append(operation)
                .append(" (:v1, :v2) ");
            paramMap.put("v1", cursor1);
            paramMap.put("v2", cursor2);
        }
        builder
            .append(" ORDER BY ")
            .append(col)
            .append(" ")
            .append(dir.name())
            .append(" , c.id ")
            .append(dir.name());
        return builder;
    }
}
