package com.ambrosia.content_service.like.model.entity;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeKey implements Serializable{
    @Column(value = "user_id")
    private UUID userId;

    @Column(value = "post_id")
    private Long postId;

    public static PostLikeKey create(UUID userId, Long postId){
        return new PostLikeKey(userId, postId);
    }
}
