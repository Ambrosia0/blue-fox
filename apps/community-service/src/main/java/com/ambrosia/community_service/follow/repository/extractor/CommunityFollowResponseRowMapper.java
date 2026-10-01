package com.ambrosia.community_service.follow.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;
import com.ambrosia.community_service.follow.model.dto.response.CommunityFollowResponse;

@Component 
public class CommunityFollowResponseRowMapper implements RowMapper<CommunityFollowResponse>{
    @Override
    public CommunityFollowResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityFollowResponse(
            new CommunityPreview(
                rs.getLong("id"),
                rs.getString("slug"),
                rs.getString("displayed_name"), 
                rs.getLong("follow_count"), 
                rs.getString("avatar_id"),
                rs.getArray("tags") != null?
                    ((String[])rs.getArray("tags").getArray()):
                    null, 
                null,
                rs.getTimestamp("created_at").toInstant()
            ),
            rs.getTimestamp("followed_at").toInstant()
        );
    }
}
