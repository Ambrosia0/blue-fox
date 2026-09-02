package com.ambrosia.outbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ambrosia.outbox.entity.KafkaOutbox;

public interface KafkaOutboxRepository extends CrudRepository<KafkaOutbox, UUID>{
    @Modifying
    @Query("""
    UPDATE kafka_outbox
        SET claimed_at = NULL
        WHERE id IN ( :toRecover )
    """)
    void recoverClaim(@Param("toRecover") List<UUID> toRecover);

    @Query("""
    WITH to_claim AS (
        SELECT * FROM kafka_outbox
        WHERE claimed_at IS NULL OR (now() - claimed_at > interval '1 minute')
        ORDER BY id ASC
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    )
    UPDATE kafka_outbox ko
    SET claimed_at = CURRENT_TIMESTAMP
    FROM to_claim tc
    WHERE ko.id = tc.id
    RETURNING ko.*
    """)
    List<KafkaOutbox> findUnclaimed(@Param("limit") Long limit);

    @Modifying
    @Query(
    """
    DELETE FROM kafka_outbox WHERE id IN ( :ids )
    """)
    void deleteByIds(@Param("ids") Iterable<UUID> ids);
}
