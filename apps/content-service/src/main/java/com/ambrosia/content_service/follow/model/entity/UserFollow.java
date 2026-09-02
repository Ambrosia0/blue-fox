package com.ambrosia.content_service.follow.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.content_service.follow.model.entity.keys.UserFollowKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "user_follow")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollow implements Persistable<UserFollowKey>{
    @Id
    private UserFollowKey id;

    @ReadOnlyProperty
    @Column("followed_at")
    private Instant followedAt;

    @Transient
    private boolean isNew = true;

    public static UserFollow create(UUID userId, UUID followedUserId){
        return new UserFollow(UserFollowKey.create(userId, followedUserId), null, true);
    }
}
