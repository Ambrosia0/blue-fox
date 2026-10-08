package com.ambrosia.content_service.post.domain.entity;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Table(name = "post_collaboration")
@AllArgsConstructor 
public class PostCollaboration {
    @Column("user_id") 
    private UUID userId;
}
