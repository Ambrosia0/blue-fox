package com.ambrosia.content_service.attachment.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("post_attachment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAttachment {
    @Id
    private Long id;

    @Column("attachment_id")
    private String attachmentId;
    
    @Column("post_id")
    private Long postId;

}
