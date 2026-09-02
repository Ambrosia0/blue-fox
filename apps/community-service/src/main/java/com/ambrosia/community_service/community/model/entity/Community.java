package com.ambrosia.community_service.community.model.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "community")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Community implements Serializable{
    @Id
    private Long id;

    @Column(value = "slug")
    private String slug;

    @Column(value = "displayed_name")
    private String displayedName;

    @Column(value = "owner_id")
    private UUID ownerId;

    @Column(value = "avatar_id")
    private String avatarId;

    @Builder.Default
    @Column(value = "description")
    private String description = "";

    @Builder.Default
    @Column(value = "is_private")
    private boolean isPrivate = false;

    @Builder.Default
    @Column(value = "follow_count")
    private Long followCount = 0L;

    @Builder.Default
    @Column(value = "post_count")
    private Long postCount = 0L;
    
    @Column(value = "rules")
    private List<String> rules;

    @UniqueElements
    @Column(value = "tags")
    private List<String> tags;

    @Version
    @Column(value = "version")
    private Long version;

    @ReadOnlyProperty
    @Column(value = "created_at")
    private Instant createdAt;
}
