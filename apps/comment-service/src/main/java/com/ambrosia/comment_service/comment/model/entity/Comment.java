package com.ambrosia.comment_service.comment.model.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="comment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable{
    @Id
    @Column(value = "id")
    private Long id;

    @Column(value = "post_id")
    private Long postId;

    @Column(value = "user_id")
    private UUID userId;

    @Column(value = "content")
    private String content;

    @Builder.Default
    @Column(value = "like_count")
    private int likeCount = 0;

    @Builder.Default
    @Column(value = "number_of_children")
    private int numberOfChildren = 0;

    @Column(value = "parent_comment_id")
    private Long parentCommentId;

    @ReadOnlyProperty
    @Column(value = "created_at")
    private Instant createdAt;

    @Builder.Default
    @Column(value = "is_visible")
    private boolean isVisible = true;
}