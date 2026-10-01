package com.ambrosia.comment_service.comment.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.user.model.dto.UserResponse;

@Component 
public class CommentDataMapper implements RowMapper<CommentData>{
    @Override
    public CommentData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommentData(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getObject("user_id", UUID.class) != null?
                new UserResponse(
                    rs.getObject("user_id", UUID.class),
                    rs.getObject("username", String.class),
                    rs.getObject("first_name", String.class),
                    rs.getObject("last_name", String.class),
                    rs.getObject("avatar_id", String.class)
                ):
                null,
            rs.getString("content"),
            rs.getInt("like_count"),
            rs.getObject("parent_comment_id", Long.class) != null?
                rs.getLong("parent_comment_id"):
                null,
            rs.getTimestamp("created_at").toInstant(),
            rs.getInt("number_of_children"),
            rs.getObject("is_liked", Boolean.class),
            rs.getString("attachment_id")
        );
    }
}
