package com.ambrosia.report_service.user.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_projection")
public class UserProjection implements Persistable<UUID>{
    @Id
    private UUID id;

    @Column("username")
    private String username;

    @Column("avatar_id")
    private String avatarId;

    @Transient
    private boolean isNew = true;
}
