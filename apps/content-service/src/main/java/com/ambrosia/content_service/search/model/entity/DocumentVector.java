package com.ambrosia.content_service.search.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Table(name = "document_vector")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVector implements Persistable<Long>{
    @Id
    private Long id;

    @Column(value = "search_vector")
    private String searchVector;
    
    @Transient
    @Builder.Default
    private boolean isNew = false;
}
