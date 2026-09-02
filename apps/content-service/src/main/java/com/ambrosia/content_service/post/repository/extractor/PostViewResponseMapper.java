package com.ambrosia.content_service.post.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;

public class PostViewResponseMapper implements RowMapper<PostViewResponse>{
    @Override
    public PostViewResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostViewResponse(
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
            rs.getObject("is_liked", Boolean.class),
            rs.getObject("name", String.class),
            rs.getObject("avatar_id", UUID.class)
        );
    }
}
