package com.ambrosia.community_service.community.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;

@Component
public class CommunityResponseRowMapper implements RowMapper<CommunityResponse>{
    @Override
    public CommunityResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityResponse(
                rs.getLong("id"),
                rs.getString("slug"),
                rs.getString("displayed_name"),
                rs.getObject("owner_id", UUID.class),
                rs.getObject("avatar_id", String.class),
                rs.getObject("description", String.class),
                rs.getArray("rules") != null?
                    (String[])rs.getArray("rules").getArray():
                    null, 
                rs.getArray("tags") != null?
                    (String[])rs.getArray("tags").getArray():
                    null, 
                rs.getArray("community_moderators") != null?
                    (UUID[])rs.getArray("community_moderators").getArray():
                    null, 
                rs.getLong("post_count"),
                rs.getLong("follow_count"),
                rs.getBoolean("is_private"),
                null,
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
