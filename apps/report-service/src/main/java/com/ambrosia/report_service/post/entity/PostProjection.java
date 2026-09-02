package com.ambrosia.report_service.post.entity;

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
@Table(name = "post_projection")
public class PostProjection implements Persistable<Long>{
    @Id
    private Long id;

    @Transient
    private boolean isNew = true;

    public static PostProjection create(Long id){
        return new PostProjection(id, true);
    }
}
