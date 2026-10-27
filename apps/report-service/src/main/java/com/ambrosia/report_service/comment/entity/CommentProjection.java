package com.ambrosia.report_service.comment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment_projection")
public class CommentProjection implements Persistable<Long>{
    @Id
    private Long id;

    @Column("version")
    private Long version;
    
    @Transient
    private boolean isNew = true;
}
