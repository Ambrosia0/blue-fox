package com.ambrosia.comment_service.community.model.entity;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
@Table(name = "community_permission")
public class CommunityPermission {
    @Column("permission")
    private String permission;

    @Column("user_id")
    private UUID userId;
}
