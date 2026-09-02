package com.ambrosia.community_service.community.repository.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;
import com.ambrosia.community_service.community.repository.CommunitySearchRepository;
import com.ambrosia.community_service.community.repository.mapper.CommunityPreviewRowMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CommunitySearchRepositoryImpl implements CommunitySearchRepository{
    private final JdbcClient jdbcClient;

    private final CommunityPreviewRowMapper communityPreviewRowMapper;

    private String baseSqlRelevant = """
    WITH ranked as (
        SELECT 
            c.id, 
            c.displayed_name,
            c.slug, 
            c.follow_count,
            c.rules,
            c.tags,
            c.avatar_id,
            c.created_at,
            GREATEST(
                similarity(c.displayed_name, :searchString),
                similarity(c.slug, :searchString)
            ) as rank
            WHERE c.displayed_name % :searchString OR c.slug % :searchString
    )
    SELECT * FROM ranked
    WHERE 1=1
    """;

    private String baseSql = """
    SELECT 
        c.id, 
        c.displayed_name,
        c.slug, 
        c.follow_count,
        c.rules, 
        c.tags,
        c.avatar_id,
        c.created_at
    FROM community c
    WHERE 1=1
    """;

    @Override
    public List<CommunityPreview> search(CommunityEventFilter eventFilter, int pageSize) {
        if(eventFilter.searchString() != null)
            return findRelevant(eventFilter, pageSize);
        else
            return findLatest(eventFilter, pageSize);
    }

    private List<CommunityPreview> findRelevant(CommunityEventFilter eventFilter, int pageSize){
        var paramMap = new LinkedHashMap<String, Object>(5, 1.0f);
        var sql = new StringBuilder(baseSqlRelevant);
        paramMap.put("searchString", eventFilter.searchString());

        applySharedFilters(eventFilter, paramMap, sql);
        if(eventFilter.lastSeenScore() != null && eventFilter.lastSeenId() != null){
            sql.append("AND (rank, id) < (:lastSeenScore, :lastSeenId) ");
            paramMap.put("lastSeenScore", eventFilter.lastSeenScore());
            paramMap.put("lastSeenId", eventFilter.lastSeenId());
        }
        if(eventFilter.direction() == Direction.DESC)
            sql.append("ORDER BY rank DESC, c.id DESC LIMIT :pageSize");
        else
            sql.append("ORDER BY rank DESC, c.id ASC LIMIT :pageSize");
        paramMap.put("pageSize", pageSize);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query(communityPreviewRowMapper)
            .list();
    }

    private List<CommunityPreview> findLatest(CommunityEventFilter eventFilter, int pageSize){
        var paramMap = new LinkedHashMap<String, Object>(5, 1.0f);
        var sql = new StringBuilder(baseSql);
        if(eventFilter.lastSeenInstant() != null && eventFilter.lastSeenId() != null){
            sql.append("AND (c.created_at, c.id) < (:lastSeenInstant, :lastSeenId) ");
            paramMap.put("lastSeenInstant", eventFilter.lastSeenInstant());
            paramMap.put("lastSeenId", eventFilter.lastSeenId());
        }
        if(eventFilter.direction() == Direction.DESC)
            sql.append("ORDER BY created_at DESC, c.id DESC LIMIT :pageSize");
        else
            sql.append("ORDER BY created_at DESC, c.id ASC LIMIT :pageSize");
        paramMap.put("pageSize", pageSize);
        return jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query(communityPreviewRowMapper)
            .list();
    }

    private void applySharedFilters(CommunityEventFilter eventFilter, Map<String, Object> paramMap, StringBuilder sql){
        if(eventFilter.tags() != null){
            sql.append("AND c.tags @> :tags ");
            paramMap.put("tags", eventFilter.tags());
        }

    }
}
