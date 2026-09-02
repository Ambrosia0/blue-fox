package com.ambrosia.comment_service.community.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.comment_service.community.model.entity.key.CommunityBanKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table
public class CommunityBanProjection implements Persistable<CommunityBanKey>{
    @Id
    private CommunityBanKey id;

    @Transient
    private boolean isNew = false;

    public static CommunityBanProjection create(long communityId, UUID userId){
        return new CommunityBanProjection(
            new CommunityBanKey(communityId, userId),
            true
        );
    }
}
