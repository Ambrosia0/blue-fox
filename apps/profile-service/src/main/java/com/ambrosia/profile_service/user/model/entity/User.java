package com.ambrosia.profile_service.user.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.profile_service.user.utils.Role;
import com.ambrosia.profile_service.user.utils.Status;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Table("service_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Persistable<UUID>{
    @Id
    private UUID id;

    @Column("username")
    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("about")
    @Builder.Default
    private String about = "";

    @Column("user_role")
    private Role role;

    @Column("is_active")
    private boolean isActive;

    @Column("is_enabled")
    private boolean isEnabled;

    @Column("email")
    private String email;

    /**
     * S3 object key of the user's avatar
     */
    @Nullable
    @Column("avatar_id")
    private String avatarId;

    @Builder.Default
    @Column("follow_count")
    private Long followCount = 0L;

    /**
     * Number of blacklisted users
     */
    @ReadOnlyProperty
    @Column("blacklist_count")
    private Short blacklistCount;

    @ReadOnlyProperty
    @Column("created_at")
    private Instant createdAt;

    @Builder.Default
    @Column("status")
    private Status status = Status.OFFLINE;

    @Builder.Default
    @Column("last_activity")
    private Instant lastActivity = Instant.now();

    @Version
    @Column("version")
    private Long version;

    @Transient
    private String password;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    @Override
    public boolean isNew(){
        return isNew;
    }
}