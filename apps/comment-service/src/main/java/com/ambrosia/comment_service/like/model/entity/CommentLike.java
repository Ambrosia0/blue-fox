package com.ambrosia.comment_service.like.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.comment_service.like.model.entity.keys.CommentLikeKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentLike implements Persistable<CommentLikeKey>{
    @Id
    private CommentLikeKey id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    public static CommentLike create(Long commentId, UUID userId){
        return CommentLike.builder().id(new CommentLikeKey(commentId, userId)).build();
    }
    public static CommentLike create(Long commentId, String userId){
        return CommentLike.builder().id(new CommentLikeKey(commentId, UUID.fromString(userId))).build();
    }
}
