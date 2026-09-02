package com.ambrosia.comment_service.comment.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.model.dto.response.TreeCommentData;

@Component
public class TreeCommentDataMapper implements RowMapper<TreeCommentData>{
    @Override
    public TreeCommentData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TreeCommentData(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getObject("user_id", UUID.class),
            rs.getString("content"),
            rs.getInt("like_count"),
            rs.getObject("parent_comment_id", Long.class),
            rs.getTimestamp("created_at").toInstant(),
            rs.getInt("number_of_children"),
            null,
            rs.getFloat("hot_score"),
            rs.getString("attachment_id")
        );
    }
}
