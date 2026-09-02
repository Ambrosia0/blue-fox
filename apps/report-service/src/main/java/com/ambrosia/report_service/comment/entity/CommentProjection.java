package com.ambrosia.report_service.comment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
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

    @Transient
    private boolean isNew = true;

    public static CommentProjection create(Long id){
        return new CommentProjection(id, true);
    }
}
