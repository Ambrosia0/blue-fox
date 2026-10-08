package com.ambrosia.profile_service.blacklist.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.profile_service.blacklist.domain.repository.BlacklistRepository;
import com.ambrosia.profile_service.blacklist.infrastructure.entity.Blacklist;
import com.ambrosia.profile_service.blacklist.infrastructure.entity.key.BlacklistKey;

public interface JdbcBlacklistRepository extends 
        Repository<Blacklist, BlacklistKey>,
        BlacklistRepository{
    @Query("SELECT blacklisted_user_id FROM blacklist WHERE user_id = :userId")
    List<UUID> findByUserId(UUID userId);

    @Modifying 
    @Query("""        
    WITH inserted_blacklist AS(
        INSERT INTO blacklist(user_id, blacklisted_user_id, reason)
        VALUES(
            :userId, 
            :blacklistedUser, 
            :reason
        )
        ON CONFLICT (user_id, blacklisted_user_id) DO NOTHING
        RETURNING *
    )
    UPDATE service_user
    SET blacklist_count = blacklist_count + 1
    FROM inserted_blacklist
        WHERE id = inserted_blacklist.user_id
    """)
    void add(UUID userId, UUID blacklistedUser, String reason);

    Blacklist save(Blacklist blacklist);

    @Modifying
    @Query("""
    WITH deleted AS(
        DELETE FROM blacklist 
        WHERE user_id = :userId
        AND blacklisted_user_id = :blacklistedUser
        RETURNING user_id
    )
    UPDATE service_user 
    SET blacklist_count = blacklist_count - 1
    FROM deleted
        WHERE id = user_id
    """)
    void remove(UUID userId, UUID blacklistedUser);

    @Modifying
    @Query("""
    WITH deleted AS(
        DELETE FROM blacklist
    )
    UPDATE service_user SET blacklist_count = 0
    """)
    void deleteAll();
}
