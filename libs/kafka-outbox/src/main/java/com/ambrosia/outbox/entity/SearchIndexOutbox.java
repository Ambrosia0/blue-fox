package com.ambrosia.outbox.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.github.robsonkades.uuidv7.UUIDv7;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "search_index_outbox")
public class SearchIndexOutbox implements Persistable<UUID> {
    @Id
    private UUID id;

    @Column("resource_id")
    private String resourceId;

    @Column("entity_type")
    private String entityType;

    @Column("payload")
    private String payload;

    @Transient
    private boolean isNew = true;

    public static SearchIndexOutbox from(String resourceId, String entityType, String payload){
        return new SearchIndexOutbox(
            UUIDv7.randomUUID(),
            resourceId,
            entityType,
            payload,
            true
        );
    }
}
