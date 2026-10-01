package com.ambrosia.content_service.community.model.entity;

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

@Table(name = "community_projection")
@AllArgsConstructor
@NoArgsConstructor
@Getter 
@Builder
public class CommunityProjection implements Persistable<Long>{
    @Id
    private Long id;

    @Column(value = "name")
    private String name;

    @Column(value = "avatar_id")
    private String avatarId;

    @Column(value = "slug")
    private String slug;

    @Column(value = "is_private")
    private boolean isPrivate;

    @Getter(value = AccessLevel.NONE)
    @MappedCollection(idColumn = "community_id")
    private Set<CommunityPermission> permissions;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    public boolean isNew(){
        return isNew;
    }
    
    public Set<CommunityPermission> getPermissions(){
        return Collections.unmodifiableSet(permissions);
    }
}
