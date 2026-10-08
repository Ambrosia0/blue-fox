package com.ambrosia.content_service.post.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.api.dto.response.CommunityResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorContentResponse;

@Component 
public class PostEditorContentResponseMapper implements RowMapper<PostEditorContentResponse>{
    @Override
    public PostEditorContentResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostEditorContentResponse(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getArray("tags") != null?
                Arrays.asList((String[])rs.getArray("tags").getArray()):
                null, 
            rs.getArray("attachment_ids") != null?
                Arrays.asList((String[])rs.getArray("attachment_ids").getArray()):
                null,
            rs.getObject("community_id", Long.class) != null?
                new CommunityResponse(
                    rs.getLong("community_id"),
                    rs.getString("name"),
                    rs.getString("slug"),
                    rs.getBoolean("is_private"),
                    rs.getObject("avatar_id", String.class)
                ):
                null,
            rs.getTimestamp("updated_at").toInstant(), 
            rs.getLong("version")
        );
    }
}
