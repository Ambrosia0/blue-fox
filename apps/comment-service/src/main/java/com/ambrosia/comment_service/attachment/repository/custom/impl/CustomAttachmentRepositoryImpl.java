package com.ambrosia.comment_service.attachment.repository.custom.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.attachment.model.entity.CommentAttachment;
import com.ambrosia.comment_service.attachment.repository.custom.CustomAttachmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomAttachmentRepositoryImpl implements CustomAttachmentRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchDelete(List<CommentAttachment> toDelete) {
        var sql = "DELETE FROM comment_attachment WHERE attachment_id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(CommentAttachment delete: toDelete){
                stmt.setString(1, delete.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched delete of attachments on database!", e);
            throw new RuntimeException(e);
        }
    }
}
