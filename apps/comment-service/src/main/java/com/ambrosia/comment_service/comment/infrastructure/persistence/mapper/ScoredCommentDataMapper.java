package com.ambrosia.comment_service.comment.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.api.dto.response.ScoredCommentData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ScoredCommentDataMapper implements RowMapper<ScoredCommentData>{
    private final CommentDataMapper commentDataMapper;

    @Override
    public ScoredCommentData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ScoredCommentData(
            commentDataMapper.mapRow(rs, rowNum),
            rs.getFloat("hot_score")
        );
    }
}
