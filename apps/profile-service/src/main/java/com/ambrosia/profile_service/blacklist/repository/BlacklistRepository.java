package com.ambrosia.profile_service.blacklist.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.profile_service.blacklist.model.entity.Blacklist;
import com.ambrosia.profile_service.blacklist.model.entity.key.BlacklistKey;

public interface BlacklistRepository extends Repository<Blacklist, BlacklistKey>{
    @Query("SELECT blacklisted_user_id FROM blacklist WHERE user_id = :userId")
    List<UUID> findByUserId(UUID userId);

    @Query("""        
    WITH inserted_blacklist AS(
        INSERT INTO blacklist(user_id, blacklisted_user_id, reason)
        VALUES(
            :#{#blacklist.id.userId}, 
            :#{#blacklist.id.blacklistedUserId}, 
            :#{#blacklist.reason}
        )
        ON CONFLICT (user_id, blacklisted_user_id) DO NOTHING
        RETURNING *
    ),
    updated_user AS (
        UPDATE service_user
        SET blacklist_count = blacklist_count + 1
        FROM inserted_blacklist
            WHERE id = inserted_blacklist.user_id
    )
    SELECT * FROM inserted_blacklist
    """)
    Blacklist save(Blacklist blacklist);

    @Modifying
    @Query("""
    WITH deleted AS(
        DELETE FROM blacklist 
        WHERE user_id = :#{#id.userId} 
        AND blacklisted_user_id = :#{#id.blacklistedUserId}
        RETURNING user_id
    )
    UPDATE service_user 
    SET blacklist_count = blacklist_count - 1
    FROM deleted
        WHERE id = user_id
    """)
    void deleteById(BlacklistKey id);

    @Modifying
    @Query("""
    WITH deleted AS(
        DELETE FROM blacklist
    )
    UPDATE service_user SET blacklist_count = 0
    """)
    void deleteAll();
}
