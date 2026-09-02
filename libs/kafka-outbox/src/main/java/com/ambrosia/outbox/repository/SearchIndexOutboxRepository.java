package com.ambrosia.outbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ambrosia.outbox.entity.SearchIndexOutbox;

public interface SearchIndexOutboxRepository extends CrudRepository<SearchIndexOutbox, UUID>{
    @Modifying
    @Query("""
    UPDATE search_index_outbox
        SET claimed_at = NULL
        WHERE id IN ( :toRecover )
    """)
    void recoverClaim(@Param("toRecover") List<UUID> toRecover);

    @Query("""
    WITH to_claim AS (
        SELECT * FROM search_index_outbox
        WHERE claimed_at IS NULL OR (now() - claimed_at > interval '3 minute')
        ORDER BY id ASC
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    )
    UPDATE search_index_outbox so
    SET claimed_at = CURRENT_TIMESTAMP
    FROM to_claim tc
    WHERE so.id = tc.id
    RETURNING so.*
    """)
    List<SearchIndexOutbox> findUnclaimed(@Param("limit") Long limit);

    @Modifying
    @Query("""
    DELETE FROM search_index_outbox WHERE id IN ( :ids )
    """)
    void deleteByIds(@Param("ids") List<UUID> ids);
}
