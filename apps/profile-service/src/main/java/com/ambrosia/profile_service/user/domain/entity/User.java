package com.ambrosia.profile_service.user.domain.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.profile_service.user.utils.Role;
import com.ambrosia.profile_service.user.utils.Status;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Table("service_user")
@AllArgsConstructor(
    onConstructor_ = @PersistenceCreator,
    access = AccessLevel.PACKAGE
)
@Getter
public class User implements Persistable<UUID>{
    @Id
    private UUID id;

    @Setter 
    @Column("username")
    private String username;

    @Setter 
    @Column("first_name")
    private String firstName;

    @Setter 
    @Column("last_name")
    private String lastName;

    @Setter 
    @Column("about")
    private String about = "";

    @Setter 
    @Column("user_role")
    private Role role;

    @Column("is_active")
    private boolean isActive;

    @Setter 
    @Column("is_enabled")
    private boolean isEnabled;

    @Setter 
    @Column("email")
    private String email;

    /**
     * S3 object key of the user's avatar
     */
    @Setter 
    @Nullable
    @Column("avatar_id")
    private String avatarId;

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

    @Column("status")
    private Status status = Status.OFFLINE;

    @Column("last_activity")
    private Instant lastActivity = Instant.now();

    @Version
    @Column("version")
    private Long version;

    @MappedCollection(idColumn = "user_id")
    private UserSettings userSettings;

    @Transient
    private String password;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew(){
        return isNew;
    }

    @Builder 
    private User(
            UUID id,
            String username,
            String firstName,
            String lastName,
            String password,
            Role role,
            boolean isEnabled,
            String avatarId,
            String email
    ){
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isEnabled = isEnabled;
        this.avatarId = avatarId;
        this.email = email;
        this.userSettings = UserSettings.builder()
            .id(id)
            .build();
        this.password = password;
    }

    public void changeSettings(UserSettings settings){
        this.userSettings = settings;
    }
}