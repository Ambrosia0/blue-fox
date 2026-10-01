package com.ambrosia.content_service.search.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.repository.extractor.PostViewResponseMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class UnscoredPreviewMapper implements RowMapper<PreviewWithScoreResponse>{
    private final PostViewResponseMapper postViewResponseMapper;

    @Override
    public PreviewWithScoreResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PreviewWithScoreResponse(
                postViewResponseMapper.mapRow(rs, rowNum),
                null
            );
    }
}
