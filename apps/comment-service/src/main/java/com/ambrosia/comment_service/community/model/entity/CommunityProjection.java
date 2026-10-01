package com.ambrosia.comment_service.community.model.entity;

import java.util.Collections;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CommunityProjection implements Persistable<Long>{
    @Id
    private Long id;

    @Column("is_private")
    private boolean isPrivate;

    @Getter(value = AccessLevel.NONE)
    @MappedCollection(idColumn = "community_id")
    private Set<CommunityPermission> permissions;

    @Builder.Default
    @Transient
    private boolean isNew = true;

    public Set<CommunityPermission> getPermissions(){
        return Collections.unmodifiableSet(permissions);
    }
}

