package org.ambrosia.notification_service.projections.infrastructure.persistence.impl;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.CommunityRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class VertxCommunityRepository implements CommunityRepository {
    private final Pool pool;
    
    private String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES ($1)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    ),
    inserted_community AS (
       INSERT INTO community(id, displayed_name, avatar_id, slug, is_private, version)
       SELECT $2, $3, $4, $5, $6, $7
       FROM inserted
       ON CONFLICT(id) DO UPDATE
        SET displayed_name = excluded.displayed_name,
            avatar_id = excluded.avatar_id,
            slug = excluded.slug,
            is_private = excluded.is_private,
            version = excluded.version
        WHERE community.deleted_at IS NULL
           AND community.version < excluded.version
        RETURNING *
    ),
    deleted_permissions AS (
      DELETE FROM community_permission cp
        USING inserted_community ic
        WHERE cp.community_id = ic.id
        RETURNING cp.community_id
    )
    INSERT INTO community_permission(community_id, user_id)
    SELECT ic.id, x.user_id
    FROM inserted_community ic, deleted_permissions
    CROSS JOIN unnest($8::uuid[]) AS x(user_id)
    CROSS JOIN (SELECT COUNT(*) as deleted_count FROM deleted_permissions) dp
    """;
    
    private String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES ($1)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    ),
    deleted_community AS (
        UPDATE community
            SET slug = NULL,
            displayed_name = NULL,
            avatar_id = NULL,
            is_private = NULL,
            deleted_at = CURRENT_TIMESTAMP
        WHERE id = $2
        AND version = $3
        RETURNING id
    ),
    deleted_permissions AS (
        DELETE FROM community_permission
        WHERE community_id = $2
    )
    DELETE FROM community_follow
    WHERE community_id = $2
    """;
    
    public VertxCommunityRepository(Pool pool) {
        this.pool = pool;
    }
    
    @Override
    public Uni<Void> process(List<CommunityEvent> toUpsert, List<CommunityEvent> toDelete) {
        return pool.withTransaction(conn -> upsert(conn, toUpsert)
            .chain(() -> delete(conn, toDelete))
        );
    }
    
    private Uni<Void> upsert(SqlConnection connection, List<CommunityEvent> events){
        if(events.isEmpty())
            return Uni.createFrom().voidItem();
        return connection.preparedQuery(UPSERT_SQL)
            .executeBatch(getUpsertParams(events))
            .replaceWithVoid();
    }
    
    private Uni<Void> delete(SqlConnection connection, List<CommunityEvent> events){
        if(events.isEmpty())
            return Uni.createFrom().voidItem();
        return connection.preparedQuery(DELETE_SQL)
            .executeBatch(getDeleteParams(events))
            .replaceWithVoid();
    }
    
    private List<Tuple> getUpsertParams(List<CommunityEvent> toUpsert){
        return toUpsert.stream()
            .map(t -> Tuple.from(
                switch (t.getEventCase()){
                    case CREATE ->List.<Object>of(
                        t.getEventId(),
                        t.getId(),
                        t.getCreate().getName(),
                        t.getCreate().getAvatarId(),
                        t.getCreate().getSlug(),
                        t.getCreate().getIsPrivate(),
                        t.getVersion(),
                        t.getCreate().getCommunityPermissionsMap()
                            .keySet()
                            .stream()
                            .map(UUID::fromString)
                            .toArray(UUID[]::new)
                    );
                    case UPDATE -> List.<Object>of(
                        t.getEventId(),
                        t.getId(),
                        t.getUpdate().getName(),
                        t.getUpdate().getAvatarId(),
                        t.getUpdate().getSlug(),
                        t.getUpdate().getIsPrivate(),
                        t.getVersion(),
                        t.getCreate().getCommunityPermissionsMap()
                            .keySet()
                            .stream()
                            .map(UUID::fromString)
                            .toArray(UUID[]::new)
                    );
                    default -> throw new IllegalArgumentException("Invalid event case in upsert!");
                })
            )
            .toList();
    }
    
    public List<Tuple> getDeleteParams(List<CommunityEvent> toDelete){
        return toDelete.stream()
            .map(t -> Tuple.of(
                t.getEventId(),
                t.getId(),
                t.getVersion()
            ))
            .toList();
    }
}
