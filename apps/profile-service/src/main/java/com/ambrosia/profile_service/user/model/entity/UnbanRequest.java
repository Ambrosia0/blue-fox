package com.ambrosia.profile_service.user.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "unban_request")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnbanRequest {
    @Id
    private Long id;

    @Column("user_id")
    private UUID userId;

    @Column("request")
    private String request;

    @Column("is_viewed")
    private boolean isViewed;

    @ReadOnlyProperty
    @Column("created_at")
    private Instant createdAt;

    @MappedCollection(idColumn = "user_id")
    private User user;
}
