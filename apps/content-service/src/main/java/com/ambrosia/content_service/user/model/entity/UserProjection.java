package com.ambrosia.content_service.user.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
@Table(name = "user_projection")
public class UserProjection implements Persistable<UUID> {
    @Id 
    private UUID id;

    @Column("username")
    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("avatar_id")
    private String avatarId;

    @Transient 
    @Builder.Default
    private boolean isNew = false;
}
