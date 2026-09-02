package com.ambrosia.comment_service.like.model.entity.keys;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommentLikeKey{
    @Column("comment_id")
    private Long commentId;

    @Column("user_id")
    private UUID userId;
}
