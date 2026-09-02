package com.ambrosia.profile_service.user.model.entity;

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

@Data
@Builder
@Table(name = "username_history")
@AllArgsConstructor
@NoArgsConstructor
public class UsernameHistory {
    @Id
    private UUID id;

    @Column("username")
    private String username;

    @Column("user_id")
    private UUID userId;

    @ReadOnlyProperty
    @Column("changed_at")
    private Instant changedAt;
    
    public static UsernameHistory from(User user){
        return new UsernameHistory(null, user.getUsername(), user.getId(), null);
    }

    public static UsernameHistory from(String username, UUID userId){
        return new UsernameHistory(null, username, userId, null);
    }
}
