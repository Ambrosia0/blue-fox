package com.ambrosia.comment_service.comment.infrastructure.persistence.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.entity.DeletePolicyData;

@Component 
public class DeletePolicyDataRowMapper implements RowMapper<DeletePolicyData>{
    @Override
    public DeletePolicyData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DeletePolicyData(
            rs.getObject("user_id", UUID.class),
            rs.getBoolean("is_moderator")
        );
    }
}
