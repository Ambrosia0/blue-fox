package com.ambrosia.comment_service.comment.repository.custom.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.EventFilter.SortField;
import com.ambrosia.comment_service.comment.model.dto.response.RootCommentData;
import com.ambrosia.comment_service.comment.model.dto.response.TreeCommentData;
import com.ambrosia.comment_service.comment.repository.CommentQueryRepository;
import com.ambrosia.comment_service.comment.repository.extractor.RootCommentDataMapper;
import com.ambrosia.comment_service.comment.repository.extractor.RootCommentDataWithLikeMapper;
import com.ambrosia.comment_service.comment.repository.extractor.TreeCommentDataMapper;
import com.ambrosia.comment_service.comment.repository.extractor.TreeCommentDataWithLikeMapper;
import com.ambrosia.comment_service.core.AppConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CustomCommentQueryRepositoryImpl implements CommentQueryRepository {
    private final JdbcClient jdbcClient;
    
    private final AppConfiguration appConfiguration;

    private final RootCommentDataMapper rootCommentDataMapper;
    private final TreeCommentDataMapper treeCommentDataMapper;
    private final RootCommentDataWithLikeMapper rootCommentDataWithLikeMapper;
    private final TreeCommentDataWithLikeMapper treeCommentDataWithLikeMapper;
    
    private static Map<SortField, String> columnMap = Map.of(
        SortField.DATE, "c.created_at",
        SortField.LIKES, "c.like_count"
    );


    @Override
    public List<RootCommentData> getRootCommentsForPost(long postId, EventFilter eventFilter, int pageSize) {
        var paramMap = new LinkedHashMap<String, Object>();
        var builder = switch(eventFilter.sortField()){
            case DATE, LIKES -> buildRootRequest(eventFilter, paramMap, false);
            case HOT -> buildRootHotRequest(eventFilter, paramMap, false);
            default -> buildRootHotRequest(eventFilter, paramMap, false);
        };
        paramMap.put("postId", postId);

        builder.append(" LIMIT :pageSize");
        paramMap.put("pageSize", pageSize);

        return jdbcClient
            .sql(builder.toString())
            .params(paramMap)
            .query(rootCommentDataMapper)
            .list();
    }

    @Override
    public List<RootCommentData> getRootCommentsForPostWithLike(long postId, UUID userId, EventFilter eventFilter, int pageSize) {
        var paramMap = new LinkedHashMap<String, Object>();
        var builder = switch(eventFilter.sortField()){
            case DATE, LIKES -> buildRootRequest(eventFilter, paramMap, true);
            case HOT -> buildRootHotRequest(eventFilter, paramMap, true);
            default -> buildRootHotRequest(eventFilter, paramMap, true);
        };
        paramMap.put("userId", userId);
        paramMap.put("postId", postId);
        
        builder.append(" LIMIT :pageSize");
        paramMap.put("pageSize", pageSize);

        return jdbcClient
            .sql(builder.toString())
            .params(paramMap)
            .query(rootCommentDataWithLikeMapper)
            .list();
    }

    @Override
    public List<TreeCommentData> getTreeForPostCommentWithLike(long commentId, UUID userId) {
        var query = 
        """
        WITH RECURSIVE comment_tree AS ( 
            SELECT 
                id,
                post_id,
                parent_comment_id,
                user_id,
                content,
                like_count,
                number_of_children,
                created_at,
                is_visible
            FROM comment
            WHERE id = :commentId
            -- AND id IS NULL

            UNION ALL

            SELECT 
                c.id,
                c.post_id,
                c.parent_comment_id,
                c.user_id,
                c.content,
                c.like_count,
                c.number_of_children,
                c.created_at,
                c.is_visible
            FROM comment c
            JOIN comment_tree ct ON c.parent_comment_id = ct.id
        )
        SELECT
            ct.id,
            ct.post_id,
            ct.user_id,
            ct.content,
            ct.parent_comment_id,
            ct.like_count,
            ct.number_of_children, 
            ct.created_at,
            ca.attachment_id,
            (cl.comment_id IS NOT NULL) as is_liked,
            CASE 
                WHEN ct.parent_comment_id IS NOT NULL THEN(
                    (log(1 + ct.like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - ct.created_at)) + 1)))
                )
            END AS hot_score
        FROM comment_tree ct
        LEFT JOIN comment_attachment ca ON ca.comment_id = ct.id
        LEFT JOIN comment_like cl ON cl.comment_id = ct.id AND cl.user_id = :userId
        WHERE ct.id != :commentId
        ORDER BY ct.parent_comment_id NULLS FIRST, ct.created_at ASC
        """;
        return jdbcClient
                .sql(query)
                .param("commentId", commentId)
                .param("userId", userId)
                .param("coeff", appConfiguration.getTimeAffectionCoefficient())
                .query(treeCommentDataWithLikeMapper)
                .list();
    }

    @Override
    public List<TreeCommentData> getTreeForPostComment(long commentId) {
        var query = 
        """
        WITH RECURSIVE comment_tree AS ( 
            SELECT 
                id,
                post_id,
                parent_comment_id,
                user_id,
                content,
                like_count,
                number_of_children,
                created_at,
                is_visible
            FROM comment
            WHERE id = :commentId
            -- AND id IS NULL

            UNION ALL

            SELECT 
                c.id,
                c.post_id,
                c.parent_comment_id,
                c.user_id,
                c.content,
                c.like_count,
                c.number_of_children,
                c.created_at,
                c.is_visible
            FROM comment c
            JOIN comment_tree ct ON c.parent_comment_id = ct.id
        )
        SELECT
            ct.id,
            ct.post_id,
            ct.user_id,
            ct.content,
            ct.parent_comment_id,
            ct.like_count,
            ct.number_of_children, 
            ct.created_at,
            ca.attachment_id,
            CASE 
                WHEN ct.parent_comment_id IS NOT NULL THEN(
                    (log(1 + ct.like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - ct.created_at)) + 1)))
                )
            END AS hot_score
        FROM comment_tree ct
        LEFT JOIN comment_attachment ca ON ca.comment_id = ct.id
        WHERE ct.id != :commentId
        ORDER BY ct.parent_comment_id NULLS FIRST, ct.created_at ASC
        """;
        return jdbcClient
                .sql(query)
                .param("commentId", commentId)
                .param("coeff", appConfiguration.getTimeAffectionCoefficient())
                .query(treeCommentDataMapper)
                .list();
    }

    @Override
    public Optional<TreeCommentData> getComment(long commentId) {
        var sql = """
            SELECT * FROM comment c
            LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
            WHERE c.id = :commentId
            """;
        return jdbcClient
            .sql(sql)
            .param("commentId", commentId)
            .query((RowMapper<TreeCommentData>)(row, metadata) -> new TreeCommentData(
                        row.getLong("id"),
                        row.getLong("post_id"),
                        row.getObject("user_id", UUID.class),
                        row.getString("content"),
                        row.getInt("like_count"),
                        row.getLong("parent_comment_id"),
                        row.getTimestamp("created_at").toInstant(),
                        row.getInt("number_of_children"),
                        null,
                        null,
                        row.getString("attachment_id")
                    ))
            .optional();
    }

    @Override
    public Optional<TreeCommentData> getCommentWithLike(long commentId, UUID userId) {
        var sql = """
            SELECT 
                *, 
                EXISTS(SELECT 1 FROM comment_like WHERE comment_id = :commentId AND user_id = :userId) as is_liked
            FROM comment c
            LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
            WHERE c.id = :commentId
            """;
        return jdbcClient
            .sql(sql)
            .param("commentId", commentId)
            .param("userId", userId)
            .query((RowMapper<TreeCommentData>)(row, metadata) -> new TreeCommentData(
                        row.getLong("id"),
                        row.getLong("post_id"),
                        row.getObject("user_id", UUID.class),
                        row.getString("content"),
                        row.getInt("like_count"),
                        row.getLong("parent_comment_id"),
                        row.getTimestamp("created_at").toInstant(),
                        row.getInt("number_of_children"),
                        row.getBoolean("is_liked"),
                        null,
                        row.getString("attachment_id")
                    ))
            .optional();
    }

    private StringBuilder buildRootHotRequest(EventFilter eventFilter, LinkedHashMap<String, Object> paramMap, boolean withLike){
        var buider = new StringBuilder(withLike?
            """
            SELECT 
                id, 
                post_id, 
                user_id, 
                content, 
                like_count, 
                number_of_children, 
                created_at, 
                is_visible,
                ca.attachment_id, 
                (log(1 + like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - created_at)) + 1))) AS hot_score, 
                EXISTS(SELECT 1 FROM comment_like cl WHERE cl.comment_id = id AND cl.user_id = :userId) as is_liked
            FROM comment
            LEFT JOIN comment_attachment ca ON ca.comment_id = id
            WHERE post_id = :postId AND parent_comment_id IS NULL 
            """:
            """
            SELECT 
                id, 
                post_id, 
                user_id, 
                content, 
                like_count, 
                number_of_children,
                created_at, 
                is_visible,
                (log(1 + like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - created_at)) + 1))) AS hot_score,
                ca.attachment_id
            FROM comment 
            LEFT JOIN comment_attachment ca ON ca.comment_id = id
            WHERE post_id = :postId 
            """);

        paramMap.put("coeff", appConfiguration.getTimeAffectionCoefficient());
        if(eventFilter.lastSeenId() != null){
            buider.append("AND id < :lastId ");
            paramMap.put("lastId", eventFilter.lastSeenId());
        }
        buider.append("ORDER BY hot_score DESC, id DESC ");
        return buider;
    }

    private StringBuilder buildRootRequest(EventFilter eventFilter, LinkedHashMap<String, Object> paramMap, boolean withLike){
        var builder = new StringBuilder(withLike?
            """
            SELECT 
                c.id, 
                c.post_id, 
                c.user_id, 
                c.content, 
                c.like_count, 
                c.number_of_children, 
                c.created_at,
                ca.attachment_id,
                c.is_visible, 
                EXISTS(SELECT 1 FROM comment_like WHERE comment_id = c.id AND user_id = :userId) as is_liked 
            FROM comment c 
            LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
            WHERE c.post_id = :postId AND c.parent_comment_id IS NULL 
            """:
            """
            SELECT 
                c.id, 
                c.post_id, 
                c.user_id, 
                c.content, 
                c.like_count, 
                c.number_of_children, 
                c.created_at,
                ca.attachment_id,
                c.is_visible 
            FROM comment c 
            LEFT JOIN comment_attachment ca ON ca.comment_id = c.id
            WHERE c.post_id = :postId AND c.parent_comment_id IS NULL
            """
        );
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
