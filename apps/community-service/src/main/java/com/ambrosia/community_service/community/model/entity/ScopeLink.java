package com.ambrosia.community_service.community.model.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.community.model.entity.keys.ScopeLinkKey;
import com.ambrosia.community_service.community.utils.ScopeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "scope_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScopeLink implements Persistable<ScopeLinkKey>, Serializable{
    @Id
    private ScopeLinkKey id;

    @Transient
    private boolean isNew = false;

    public static ScopeLink create(UUID userId, Short scopeId, Long communityId){
        return new ScopeLink(ScopeLinkKey.create(userId, scopeId, communityId), true);
    }
    public static ScopeLink create(UUID userId, Short scopeId, Long communityId, boolean isNew){
        return new ScopeLink(ScopeLinkKey.create(userId, scopeId, communityId), isNew);
    }

    public static List<ScopeLink> create(UUID userId, ScopeEnum[] scopes, Long communityId){
        return Arrays.asList(scopes)
            .stream()
            .map(scopeEnum -> create(userId, scopeEnum.getId(), communityId))
            .toList();
    }
}
