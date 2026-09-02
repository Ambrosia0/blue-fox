package com.ambrosia.comment_service.post.model.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "post_projection")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostProjection implements Persistable<Long>, Serializable {
    @Id
    @Column("post_id")
    private Long id;

    @Column("community_id")
    private Long communityId;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    public boolean isNew(){
        return isNew;
    }
}
