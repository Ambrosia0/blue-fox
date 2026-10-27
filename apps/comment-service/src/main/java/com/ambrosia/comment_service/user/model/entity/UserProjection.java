package com.ambrosia.comment_service.user.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

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
    
    @Column("version")
    private Long version;

    @Transient 
    @Builder.Default
    private boolean isNew = false;
}
