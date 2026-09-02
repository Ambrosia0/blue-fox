package com.ambrosia.content_service.search.repository.impl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.SearchType;
import com.ambrosia.content_service.search.model.dto.EventFilter.SortField;
import com.ambrosia.content_service.search.repository.PostSearchRepository;

import lombok.RequiredArgsConstructor;

/**
 * Search variants are separated according to different approaches to filtration of the resulting table:
 * - Latest - date filtration;
 * - Best - like_count filtration;
 * - Popular - filtration based on application of decay function to like_count and publication date;
 * - Individual - usage of external tables (follows) to filter output, possibly applying decay function.
 * Only for authorized users.
 * - Relevant - filtering based on a relevance score calculated from lexical analysis 
 * of post tokens using a RUM index.
 * Latest and Popular shares some filters, such as @param authorId and @param searchString
 */
@RequiredArgsConstructor
@Repository
public class PostSearchRepositoryImpl implements PostSearchRepository{
    private final JdbcClient jdbcClient;

    private float timeAffectionCoefficient = 0.7f;
    private String baseSql = """
    SELECT 
        p.id,
        p.title,
        p.author_id,
        p.preview,
        p.tags,
        p.community_id,
        p.like_count,
        p.comment_count,
        p.view_count,
        p.published_at,
        cp.name,
        cp.avatar_id
    """;

    @Override
    public List<PreviewWithScoreResponse> search(
            EventFilter eventFilter, 
            UUID requestingUser, 
            int pageSize,
            List<UUID> blacklist
        ) {
        return switch(eventFilter.searchType()){
            case LATEST -> findLatest(eventFilter, requestingUser, pageSize, blacklist);
            case PERSONALIZED -> findIndividual(eventFilter, requestingUser, pageSize, blacklist);
            case POPULAR -> findPopular(eventFilter, requestingUser, pageSize, blacklist);
            case BEST -> findBest(eventFilter, requestingUser, pageSize, blacklist);
            case RELEVANCY -> findRelevant(eventFilter, requestingUser, pageSize, blacklist);
            default -> findPopular(eventFilter, requestingUser, pageSize, blacklist);
        };
    }

    private List<PreviewWithScoreResponse> findRelevant(
            EventFilter eventFilter, 
            UUID userId, 
            int pageSize,
            List<UUID> blacklist){
        var paramMap = new LinkedHashMap<String, Object>();
        var sql = new StringBuilder(baseSql);
        if(userId != null)
            applyLikeCalculaction(paramMap, sql, userId);
        applyRankCalculation(eventFilter, paramMap, sql);
        applyDocumentFromItem(sql);
        applyInitWhereCondition(sql);
        applySharedFilters(eventFilter, paramMap, sql, userId, blacklist);
        applyRelevancyFilter(eventFilter, paramMap, sql);
        applyLimit(paramMap, sql, pageSize);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query((RowMapper<PreviewWithScoreResponse>)(rs, rowNum) -> new PreviewWithScoreResponse(
                new PostViewResponse(
                    rs.getLong("id"),
                    rs.getObject("author_id", UUID.class),
                    rs.getString("title"),
                    rs.getString("preview"),
                    rs.getArray("tags") == null?
                        null:
                        Arrays.asList((String[])rs.getArray("tags").getArray()),
                    rs.getObject("community_id", Long.class),
                    rs.getInt("like_count"),
                    rs.getInt("comment_count"),
                    rs.getLong("view_count"),
                    rs.getTimestamp("published_at").toInstant(),
                    userId != null?
                        rs.getObject("is_liked", Boolean.class):
                        null,
                    rs.getObject("name", String.class),
                    rs.getObject("avatar_id", UUID.class)
                ),
                rs.getFloat("rank")
            ))
            .list();
    }

    private List<PreviewWithScoreResponse> findPopular(
            EventFilter eventFilter, 
            UUID userId, 
            int pageSize,
            List<UUID> blacklist){
        var paramMap = new LinkedHashMap<String, Object>();
        var sql = new StringBuilder(baseSql);
        if(userId != null)
            applyLikeCalculaction(paramMap, sql, userId);
        applyDecayFunction(paramMap, sql);
        if(eventFilter.searchString() != null){
            applyRankCalculation(eventFilter, paramMap, sql);
            applyDocumentFromItem(sql);
        }else{
            applyPostFromItem(sql);
        }
        applyInitWhereCondition(sql);
        applySharedFilters(eventFilter, paramMap, sql, userId, blacklist);
        applyPopularityFilter(eventFilter, paramMap, sql);
        applyLimit(paramMap, sql, pageSize);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query((RowMapper<PreviewWithScoreResponse>)(rs, rowNum) -> new PreviewWithScoreResponse(
                new PostViewResponse(
                    rs.getLong("id"),
                    rs.getObject("author_id", UUID.class),
                    rs.getString("title"),
                    rs.getString("preview"),
                    rs.getArray("tags") == null?
                        null:
                        Arrays.asList((String[])rs.getArray("tags").getArray()),
                    rs.getObject("community_id", Long.class),
                    rs.getInt("like_count"),
                    rs.getInt("comment_count"),
                    rs.getLong("view_count"),
                    rs.getTimestamp("published_at").toInstant(),
                    userId != null?
                        rs.getObject("is_liked", Boolean.class):
                        null,
                    rs.getObject("name", String.class),
                    rs.getObject("avatar_id", UUID.class)
                ),
                rs.getFloat("popularity_score")
            ))
            .list();
    }

    private List<PreviewWithScoreResponse> findLatest(
            EventFilter eventFilter, 
            UUID userId, 
            int pageSize,
            List<UUID> blacklist) {
        var paramMap = new LinkedHashMap<String, Object>();
        var sql = new StringBuilder(baseSql);
        if(userId != null)
            applyLikeCalculaction(paramMap, sql, userId);
        if(eventFilter.searchString() != null){
            applyRankCalculation(eventFilter, paramMap, sql);
            applyDocumentFromItem(sql);
        }else{
            applyPostFromItem(sql);
        }
        applyInitWhereCondition(sql);
        applySharedFilters(eventFilter, paramMap, sql, userId, blacklist);
        applyDateFilter(eventFilter, paramMap, sql);
        applyLimit(paramMap, sql, pageSize);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query((RowMapper<PreviewWithScoreResponse>)(rs, rowNum) -> new PreviewWithScoreResponse(
                new PostViewResponse(
                    rs.getLong("id"),
                    rs.getObject("author_id", UUID.class),
                    rs.getString("title"),
                    rs.getString("preview"),
                    rs.getArray("tags") == null?
                        null:
                        Arrays.asList((String[])rs.getArray("tags").getArray()),
                    rs.getObject("community_id", Long.class),
                    rs.getInt("like_count"),
                    rs.getInt("comment_count"),
                    rs.getLong("view_count"),
                    rs.getTimestamp("published_at").toInstant(),
                    userId != null?
                        rs.getObject("is_liked", Boolean.class):
                        null,
                    rs.getObject("name", String.class),
                    rs.getObject("avatar_id", UUID.class)
                ),
                 null
            ))
            .list();
    }

    private List<PreviewWithScoreResponse> findBest(
            EventFilter eventFilter, 
            UUID userId, 
            int pageSize,
            List<UUID> blacklist) {
        var paramMap = new LinkedHashMap<String, Object>();
        var sql = new StringBuilder(baseSql);
        if(userId != null)
            applyLikeCalculaction(paramMap, sql, userId);
        if(eventFilter.searchString() != null){
            applyRankCalculation(eventFilter, paramMap, sql);
            applyDocumentFromItem(sql);
        }else{
            applyPostFromItem(sql);
        }
        applyInitWhereCondition(sql);
        applySharedFilters(eventFilter, paramMap, sql, userId, blacklist);
        applyBestFilter(eventFilter, paramMap, sql);
        applyLimit(paramMap, sql, pageSize);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query((RowMapper<PreviewWithScoreResponse>)(rs, rowNum) -> new PreviewWithScoreResponse(
                new PostViewResponse(
                    rs.getLong("id"),
                    rs.getObject("author_id", UUID.class),
                    rs.getString("title"),
                    rs.getString("preview"),
                    rs.getArray("tags") == null?
                        null:
                        Arrays.asList((String[])rs.getArray("tags").getArray()),
                    rs.getObject("community_id", Long.class),
                    rs.getInt("like_count"),
                    rs.getInt("comment_count"),
                    rs.getLong("view_count"),
                    rs.getTimestamp("published_at").toInstant(),
                    userId != null?
                        rs.getObject("is_liked", Boolean.class):
                        null,
                    rs.getObject("name", String.class),
                    rs.getObject("avatar_id", UUID.class)
                ),
                 null
            ))
            .list();
    }

    private List<PreviewWithScoreResponse> findIndividual(
            EventFilter eventFilter, 
            UUID userId, 
            int pageSize,
            List<UUID> blacklist) {
        var paramMap = new LinkedHashMap<String, Object>();
        var sql = new StringBuilder(baseSql);
        if(eventFilter.sortField() == SortField.SCORE){
            applyDecayFunction(paramMap, sql);
        }
        applyLikeCalculaction(paramMap, sql, userId);
        applyPostFromItem(sql);
        applyInitWhereCondition(sql);
        applySharedFilters(eventFilter, paramMap, sql, userId, blacklist);
        applyPersonalizationFilter(sql);
        if(eventFilter.sortField() == SortField.SCORE)
            applyPopularityFilter(eventFilter, paramMap, sql);
        else
            applyDateFilter(eventFilter, paramMap, sql);
        applyLimit(paramMap, sql, 10);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query((RowMapper<PreviewWithScoreResponse>)(rs, rowNum) -> new PreviewWithScoreResponse(
                new PostViewResponse(
                    rs.getLong("id"),
                    rs.getObject("author_id", UUID.class),
                    rs.getString("title"),
                    rs.getString("preview"),
                    rs.getArray("tags") == null?
                        null:
                        Arrays.asList((String[])rs.getArray("tags").getArray()),
                    rs.getObject("community_id", Long.class),
                    rs.getInt("like_count"),
                    rs.getInt("comment_count"),
                    rs.getLong("view_count"),
                    rs.getTimestamp("published_at").toInstant(),
                    userId != null?
                        rs.getObject("is_liked", Boolean.class):
                        null,
                    rs.getObject("name", String.class),
                    rs.getObject("avatar_id", UUID.class)
                ),
                eventFilter.sortField() == SortField.SCORE? rs.getFloat("popularity_score"): null
            ))
            .list();
    }
    
    private void applyRankCalculation(EventFilter eventFilter, Map<String, Object> paramMap, StringBuilder sql){
        sql.append(",dv.search_vector <=> plainto_tsquery(:searchString) AS rank ");
        paramMap.put("searchString", eventFilter.searchString());
    }

    private void applyDecayFunction(Map<String, Object> paramMap, StringBuilder sql){
        sql.append(",(log(1 + p.like_count) + (:coeff / (EXTRACT(EPOCH FROM (now() - p.published_at)) + 1))) as popularity_score ");
        paramMap.put("coeff", timeAffectionCoefficient);
    }

    private void applyLikeCalculaction(Map<String, Object> paramMap, StringBuilder sql, UUID userId){
        sql.append(",EXISTS(SELECT 1 FROM post_like pl WHERE pl.post_id = p.id AND pl.user_id = :requestingUser) as is_liked ");
        paramMap.put("requestingUser", userId);
    }

    private void applyPostFromItem(StringBuilder sql){
        sql.append("""
        FROM post p 
        LEFT JOIN community_projection cp ON cp.id = p.community_id 
        """);
    }

    private void applyDocumentFromItem(StringBuilder sql){
        sql.append("""
        FROM document_vector dv 
        JOIN post p ON p.id = dv.id
        LEFT JOIN community_projection cp ON cp.id = p.id 
        """);
    }

    private void applyInitWhereCondition(StringBuilder sql){
        sql.append("WHERE 1=1 ");
    }
    
    private void applyPersonalizationFilter(StringBuilder sql){
        sql.append("""
            EXISTS (
                SELECT 1 FROM user_follow uf 
                WHERE uf.followed_user_id = p.author_id 
                AND user_id = :requestingUser
            ) OR
            EXISTS (
                SELECT 1 FROM community_follow cf
                WHERE cf.community_id = p.community_id
                AND user_id = :requestingUser
            )
        """);
    }

    private void applySharedFilters(
            EventFilter eventFilter, 
            Map<String, Object> paramMap, 
            StringBuilder sql, 
            UUID userId,
            List<UUID> blacklist){
        sql.append("AND p.published = true ");
        // if(eventFilter.searchString() != null){
        //     sql.append("AND p.content ILIKE :search OR p.title ILIKE :search ");
        //     paramMap.put("search", "%" + eventFilter.searchString() + "%");
        // }
        if(eventFilter.authorId() != null){
            sql.append("AND p.author_id = :authorId ");
            paramMap.put("authorId", eventFilter.authorId());
        }
        if(eventFilter.tags() != null){
            sql.append("AND p.tags @> :tags ");
            paramMap.put("tags", eventFilter.tags());
        }
        if(eventFilter.communityId() != null){
            sql.append("AND p.community_id = :communityId ");
            paramMap.put("communityId", eventFilter.communityId());
        }else if(eventFilter.searchType() != SearchType.PERSONALIZED){
            sql.append("AND (cp.is_private = 'false' OR cp.id IS NULL) ");
        }
        if(eventFilter.searchString() != null){
            sql.append("AND dv.search_vector @@ plainto_tsquery(:searchString) ");
            paramMap.computeIfAbsent("searchString", key -> eventFilter.searchString());
        }
        if(blacklist != null && !blacklist.isEmpty()){
            sql.append("AND p.author_id NOT IN ( :blacklist )");
            paramMap.put("blacklist", blacklist);
        }
    }

    private void applyRelevancyFilter(EventFilter eventFilter, Map<String, Object> paramMap, StringBuilder sql){
        if(eventFilter.lastScore() != null && eventFilter.lastSeenInstant() != null){
            if(eventFilter.direction() == Direction.DESC){
                sql.append("AND ((dv.search_vector <=> plainto_tsquery(:searchString)), p.published_at) < (:lastScore, :lastSeenInstant) ");
            }else if(eventFilter.direction() == Direction.ASC){
                sql.append("AND ((dv.search_vector <=> plainto_tsquery(:searchString)), p.published_at) > (:lastScore, :lastSeenInstant) ");
            }
            paramMap.put("lastScore", eventFilter.lastScore());
            paramMap.put("lastSeenInstant", eventFilter.lastSeenInstant());
        }
        if(eventFilter.direction() == Direction.DESC){
            sql.append("ORDER BY rank DESC, p.published_at DESC ");
        }else{
            sql.append("ORDER BY rank ASC, p.published_at ASC ");
        }
    }

    private void applyBestFilter(EventFilter eventFilter, Map<String, Object> paramMap, StringBuilder sql){
        if(eventFilter.lastSeenLikeCount() != null && eventFilter.lastSeenInstant() != null){
            if(eventFilter.direction() == Direction.DESC){
                sql.append("AND (p.like_count, p.published_at) < (:lastSeenLikeCount, :lastSeenInstant) ");
            }else if(eventFilter.direction() == Direction.ASC){
                sql.append("AND (p.like_count, p.published_at) > (:lastSeenLikeCount, :lastSeenInstant) ");
            }
            paramMap.put("lastSeenLikeCount", eventFilter.lastSeenLikeCount());
            paramMap.put("lastSeenInstant", eventFilter.lastSeenInstant());
        }
        if(eventFilter.direction() == Direction.DESC)
            sql.append("ORDER BY p.like_count DESC, p.published_at DESC ");
        else
            sql.append("ORDER BY p.like_count ASC, p.published_at ASC ");
    }

    private void applyPopularityFilter(EventFilter eventFilter, Map<String, Object> paramMap, StringBuilder sql){
        sql.append("AND published_at > (now() - interval '7 days') ");
        if(eventFilter.lastScore() != null && eventFilter.lastSeenInstant() != null){
            if(eventFilter.direction() == Direction.DESC){
                sql.append("AND (popularity_score, p.published_at) < (:lastScore, :lastSeenInstant)");
            }else if(eventFilter.direction() == Direction.ASC){
                sql.append("AND (popularity_score, p.published_at) > (:lastScore, :lastSeenInstant)");
            }
            paramMap.put("lastScore", eventFilter.lastScore());
            paramMap.put("lastSeenInstant", eventFilter.lastSeenInstant());
        }
        if(eventFilter.direction() == Direction.DESC)
            sql.append("ORDER BY popularity_score DESC, p.published_at DESC ");
        else
            sql.append("ORDER BY popularity_score ASC, p.published_at ASC ");
    }

    private void applyDateFilter(EventFilter eventFilter, Map<String, Object> paramMap, StringBuilder sql){
        if(eventFilter.lastSeenId() != null && eventFilter.lastSeenInstant() != null){
            if(eventFilter.direction() == Direction.DESC){
                sql.append("AND (p.published_at, p.id < (:lastSeenInstant, :lastSeenId)) ");
            }
            else if(eventFilter.direction() == Direction.ASC){
                sql.append("AND (p.published_at, p.id > (:lastSeenInstant, :lastSeenId)) ");
            }
            paramMap.put("lastSeenInstant", eventFilter.lastSeenInstant());
            paramMap.put("lastSeenId", eventFilter.lastSeenId());
        }
        if(eventFilter.direction() == Direction.DESC)
            sql.append("ORDER BY p.published_at DESC, p.id DESC ");
        else
            sql.append("ORDER BY p.published_at ASC, p.id ASC ");
    }

    private void applyLimit(Map<String, Object> paramMap, StringBuilder sql, int limit){
        sql.append("LIMIT :limit");
        paramMap.put("limit", limit);
    }
}
