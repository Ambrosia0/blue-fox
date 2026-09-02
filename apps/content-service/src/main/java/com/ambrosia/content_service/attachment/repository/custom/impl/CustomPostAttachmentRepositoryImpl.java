package com.ambrosia.content_service.attachment.repository.custom.impl;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.attachment.model.entity.PostAttachment;
import com.ambrosia.content_service.attachment.repository.custom.CustomPostAttachmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomPostAttachmentRepositoryImpl implements CustomPostAttachmentRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchDeleteAll(List<PostAttachment> toDelete) {
        var sql ="DELETE FROM post_attachment WHERE post_id = ? AND attachment_id = ? AND to_delete = 'true'";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(PostAttachment delete: toDelete){
                if(delete.getPostId() == null)
                    stmt.setNull(1, Types.BIGINT);
                else
                    stmt.setLong(1, delete.getPostId());
                stmt.setString(2, delete.getAttachmentId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't batch delete attachments!", e);
            throw new RuntimeException(e);
        }
    }
}
