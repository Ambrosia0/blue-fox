package com.ambrosia.report_service.community.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community_projection")
public class CommunityProjection implements Persistable<Long> {
    @Id
    private Long id;

    private boolean isNew = true;

    public static CommunityProjection create(Long id){
        return new CommunityProjection(id, true);
    }
}
