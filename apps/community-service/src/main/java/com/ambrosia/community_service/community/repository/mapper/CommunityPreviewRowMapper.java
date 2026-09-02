package com.ambrosia.community_service.community.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;

@Component
public class CommunityPreviewRowMapper implements RowMapper<CommunityPreview>{
    @Override
    public CommunityPreview mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityPreview(
                rs.getLong("id"),
                rs.getString("slug"),
                rs.getString("displayed_name"),
                rs.getLong("follow_count"),
                rs.getObject("avatar_id", String.class),
                rs.getArray("tags") == null? 
                    null: 
                    (String[])rs.getArray("tags").getArray(),
                null,
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
