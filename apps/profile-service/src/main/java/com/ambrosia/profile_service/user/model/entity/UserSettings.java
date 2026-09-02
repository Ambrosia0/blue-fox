package com.ambrosia.profile_service.user.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_settings")
public class UserSettings implements Persistable<UUID>{
    @Id
    @Column("user_id")
    private UUID id;

    @Builder.Default
    @Column("display_email")
    private boolean displayEmail = false;

    @Builder.Default
    @Column("display_activity")
    private boolean displayActivity = true;

    @Builder.Default
    @Transient
    private boolean isNew = false;
}
