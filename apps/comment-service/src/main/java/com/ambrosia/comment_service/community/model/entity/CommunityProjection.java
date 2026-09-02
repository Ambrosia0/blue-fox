package com.ambrosia.comment_service.community.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityProjection implements Persistable<Long>{
    @Id
    private Long id;

    @Column("is_private")
    private boolean isPrivate;

    @Builder.Default
    @Transient
    private boolean isNew = true;
}

