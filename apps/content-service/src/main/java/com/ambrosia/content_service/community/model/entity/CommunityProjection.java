package com.ambrosia.content_service.community.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "community_projection")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityProjection implements Persistable<Long>{
    @Id
    private Long id;

    @Column(value = "name")
    private String name;

    @Column(value = "avatar_id")
    private String avatarId;

    @Column(value = "is_private")
    private boolean isPrivate;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    public boolean isNew(){
        return isNew;
    }
    
}
