package com.ambrosia.report_service.user.entity;

import java.util.UUID;

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
@Table(name = "user_projection")
public class UserProjection implements Persistable<UUID>{
    @Id
    private UUID id;

    @Transient
    private boolean isNew = true;

    public static UserProjection create(UUID id){
        return new UserProjection(id, true);
    }
}
