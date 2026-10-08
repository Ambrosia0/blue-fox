package com.ambrosia.community_service.community.domain.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.community.utils.ScopeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "scope_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScopeLink implements Serializable{
    @Column(value = "scope_id") 
    private Short scopeId;

    @Column(value = "user_id") 
    private UUID userId;

    public static ScopeLink create(UUID userId, Short scopeId){
        return new ScopeLink(scopeId, userId);
    }

    public static List<ScopeLink> create(UUID userId, ScopeEnum[] scopes){
        return Arrays.asList(scopes)
            .stream()
            .map(scopeEnum -> create(userId, scopeEnum.getId()))
            .toList();
    }
}
