package com.ambrosia.profile_service.blacklist.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.profile_service.blacklist.model.entity.key.BlacklistKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Blacklist implements Persistable<BlacklistKey>{
    @Id
    private BlacklistKey id;

    @Column("reason")
    private String reason;

    @Transient
    private boolean isNew = false;

    public static Blacklist create(UUID userId, UUID blacklistedUserId, String reason){
        return new Blacklist(
            BlacklistKey.from(userId, blacklistedUserId),
            reason,
            true
        );
    }
}
