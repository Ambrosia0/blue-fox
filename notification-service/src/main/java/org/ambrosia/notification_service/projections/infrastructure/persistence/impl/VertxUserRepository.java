package org.ambrosia.notification_service.projections.infrastructure.persistence.impl;

import com.ambrosia.profile_service.kafka_events.UserEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.UserRepository;

import java.util.List;

@ApplicationScoped
public class VertxUserRepository implements UserRepository {
    private final Pool pool;
    
    private String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES($1)
            ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO service_user(id, username, avatar_id, version)
    SELECT $2, $3, $4, $5
    FROM inserted
    ON CONFLICT (id)
    DO UPDATE SET
        username = EXCLUDED.username,
        avatar_id = EXCLUDED.avatar_id,
        version = EXCLUDED.version
    WHERE service_user.deleted_at IS NULL
        AND service_user.version < EXCLUDED.version
    """;
    
    private String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES($1)
        RETURNING id
    )
    UPDATE service_user su
    SET username = NULL,
        avatar_id = NULL,
        notification_offset = NULL,
        deleted_at = CURRENT_TIMESTAMP
    FROM inserted
    WHERE su.id = $2
    AND su.version = $3
    """;
    
    public VertxUserRepository(Pool pool){
        this.pool = pool;
    }
    
    @Override
    public Uni<Void> process(List<UserEvent> toUpsert, List<UserEvent> toDelete) {
        return pool.withTransaction(conn -> upsert(conn, toUpsert)
            .chain(() -> delete(conn, toDelete))
        );
    }
    
    private Uni<Void> upsert(SqlConnection sqlConnection, List<UserEvent> events){
        if(events == null || events.isEmpty())
            return Uni.createFrom().voidItem();
        return sqlConnection.preparedQuery(UPSERT_SQL)
            .executeBatch(getUpsertParams(events))
            .replaceWithVoid();
    }
    
    private Uni<Void> delete(SqlConnection sqlConnection, List<UserEvent> events){
        if(events == null || events.isEmpty())
            return Uni.createFrom().voidItem();
        return sqlConnection.preparedQuery(DELETE_SQL)
            .executeBatch(getDeleteParams(events))
            .replaceWithVoid();
    }
    
    private List<Tuple> getUpsertParams(List<UserEvent> toUpsert){
        return toUpsert.stream()
            .map(t -> switch (t.getPayloadCase()){
                case CREATED -> Tuple.of(
                    t.getEventId(),
                    t.getUserId(),
                    t.getCreated().getUsername(),
                    t.getCreated().getAvatarId(),
                    t.getVersion()
                );
                case UPDATED -> Tuple.of(
                    t.getEventId(),
                    t.getUserId(),
                    t.getUpdated().getUsername(),
                    t.getUpdated().getAvatarId(),
                    t.getVersion()
                );
                default -> throw new IllegalArgumentException("Invalid payload case in creation arg!");
            })
            .toList();
    }
    
    private List<Tuple> getDeleteParams(List<UserEvent> toDelete){
        return toDelete.stream()
            .map(t ->{
                if(t.getPayloadCase() != UserEvent.PayloadCase.DELETED)
                    throw new IllegalArgumentException("Invalid payload case in deletion arg!");
                return Tuple.of(
                    t.getEventId(),
                    t.getUserId(),
                    t.getVersion()
                );
            })
            .toList();
    }
}
