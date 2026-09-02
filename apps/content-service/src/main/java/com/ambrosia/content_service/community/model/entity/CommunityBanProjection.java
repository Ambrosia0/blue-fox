package com.ambrosia.content_service.community.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.content_service.community.model.entity.key.CommunityBanKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table(name = "community_ban_projection")
public class CommunityBanProjection implements Persistable<CommunityBanKey>{
    @Id
    private CommunityBanKey id;

    @Transient
    private boolean isNew = false;
}
