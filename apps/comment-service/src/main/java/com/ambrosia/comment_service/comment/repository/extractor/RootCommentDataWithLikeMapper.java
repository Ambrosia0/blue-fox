package com.ambrosia.comment_service.comment.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.model.dto.response.RootCommentData;

@Component
public class RootCommentDataWithLikeMapper implements RowMapper<RootCommentData>{
    @Override
    public RootCommentData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RootCommentData(
            rs.getLong("id"),
            rs.getLong("post_id"), 
            rs.getObject("user_id", UUID.class),
            rs.getString("content"),
            rs.getInt("like_count"),
            rs.getInt("number_of_children"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getBoolean("is_liked"),
            rs.getString("attachment_id")
        );
    }
}
