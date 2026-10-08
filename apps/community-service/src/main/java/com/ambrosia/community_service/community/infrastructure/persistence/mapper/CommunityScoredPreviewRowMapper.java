package com.ambrosia.community_service.community.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.application.query.model.CommunityScoredPreview;
import com.ambrosia.community_service.core.infrastructure.persistence.mapper.CommunityPreviewRowMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CommunityScoredPreviewRowMapper implements RowMapper<CommunityScoredPreview>{
    private final CommunityPreviewRowMapper communityPreviewRowMapper;

    @Override
    public CommunityScoredPreview mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityScoredPreview(
            communityPreviewRowMapper.mapRow(rs, rowNum),
            rs.getObject("rank", Float.class) != null?
                rs.getFloat("rank"):
                null
        );
    }
}
