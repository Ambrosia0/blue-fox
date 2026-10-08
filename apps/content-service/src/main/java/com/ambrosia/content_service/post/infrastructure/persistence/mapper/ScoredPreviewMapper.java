package com.ambrosia.content_service.post.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class ScoredPreviewMapper implements RowMapper<PreviewWithScoreResponse>{
    private final PostViewResponseMapper postViewResponseMapper;
    
    @Override
    public PreviewWithScoreResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PreviewWithScoreResponse(
                postViewResponseMapper.mapRow(rs, rowNum),
                rs.getFloat("score")
            );
    }
}
