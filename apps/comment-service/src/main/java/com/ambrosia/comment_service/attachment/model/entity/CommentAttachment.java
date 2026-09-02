package com.ambrosia.comment_service.attachment.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "comment_attachment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentAttachment implements Persistable<String> {
    @Id
    @Column("attachment_id")
    private String id;

    @Column("comment_id")
    private Long commentId;

    @Builder.Default
    @Transient
    private boolean isNew = true;

}
