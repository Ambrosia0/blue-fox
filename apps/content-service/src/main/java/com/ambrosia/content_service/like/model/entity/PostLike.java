package com.ambrosia.content_service.like.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike implements Persistable<PostLikeKey>{
    @Id
    private PostLikeKey id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    /***
     * @see https://github.com/spring-projects/spring-data-relational/issues/2201
     * @param postLikeKey
     */
    @PersistenceCreator
    public PostLike(PostLikeKey id){
        this.id = id;
        this.isNew = true;
    }

    public static PostLike create(UUID userId, long postId){
        var key = new PostLikeKey(userId, postId);
        return new PostLike(key);
    }
    public static PostLike create(String userId, long postId){
        var key = new PostLikeKey(UUID.fromString(userId), postId);
        return new PostLike(key);
    }
}
