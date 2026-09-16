package com.ambrosia.content_service.post.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.ambrosia.content_service.post.model.dto.response.CommunityResponse;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostResponse;

public class PostContentResponseMapper implements RowMapper<PostContentResponse>{
    @Override
    public PostContentResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostContentResponse(
                rs.getLong("id"),
                rs.getObject("author_id", UUID.class),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("preview"),
                rs.getArray("tags") == null?
                    null:
                    Arrays.asList((String[])rs.getArray("tags").getArray()),
                rs.getTimestamp("published_at").toInstant(),
                null,
                rs.getInt("like_count"),
                rs.getInt("comment_count"),
                rs.getLong("view_count"),
                rs.getLong("previewed_count"),
                rs.getObject("ref_id", Long.class) != null?
                    new PostResponse(
                        rs.getLong("ref_id"),
                        rs.getString("ref_title")
                    ):
                    null,
                rs.getObject("community_id", Long.class) != null?
                    new CommunityResponse(
                        rs.getLong("community_id"),
                        rs.getString("name"),
                        rs.getBoolean("is_private"),
                        rs.getObject("avatar_id", UUID.class)
                    ):
                    null
        );
    }
}
