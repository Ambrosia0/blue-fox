package org.ambrosia.notification_service.projections.infrastructure.persistence.impl;

import com.ambrosia.content_service.kafka_events.PostEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.PostRepository;

import java.util.List;

@ApplicationScoped
public class VertxPostRepository implements PostRepository {
    private final Pool pool;
    
    private final String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES($1)
        ON CONFLICT DO NOTHING
        RETURNING id
    )
    INSERT INTO post(id, community_id, is_published, version)
    SELECT $2, $3, $4, $5
    FROM inserted
    ON CONFLICT (id)
    DO UPDATE SET
           community_id = excluded.community_id,
           is_published = excluded.is_published,
           version = excluded.version
    WHERE post.deleted_at IS NULL
           AND post.version < excluded.version
    """;
    
    private final String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES ($1)
               ON CONFLICT DO NOTHING
               RETURNING id
    )
    UPDATE post
        SET community_id = NULL,
            is_published = NULL,
            deleted_at = CURRENT_TIMESTAMP
    FROM inserted
    WHERE post.id = $2
    AND post.version = $3
    """;
    
    public VertxPostRepository(Pool pool) {
        this.pool = pool;
    }
    
    @Override
    public Uni<Void> process(List<PostEvent> toUpsert, List<PostEvent> toDelete) {
        return pool.withTransaction(conn -> upsert(conn, toUpsert)
            .chain(() -> delete(conn, toDelete))
        );
    }
    
    private Uni<Void> upsert(SqlConnection connection, List<PostEvent> postEvents){
        if(postEvents.isEmpty())
            return Uni.createFrom().voidItem();
        return connection
            .preparedQuery(UPSERT_SQL)
            .executeBatch(getUpsertParams(postEvents))
            .replaceWithVoid();
    }
    
    private Uni<Void> delete(SqlConnection connection, List<PostEvent> postEvents){
        if(postEvents.isEmpty())
            return Uni.createFrom().voidItem();
        return connection
            .preparedQuery(DELETE_SQL)
            .executeBatch(getDeleteParams(postEvents))
            .replaceWithVoid();
    }
    
    private List<Tuple> getUpsertParams(List<PostEvent> toUpsert){
        return toUpsert.stream()
            .map(t -> switch (t.getEventCase()){
                case CREATED -> Tuple.of(
                    t.getEventId(),
                    t.getPostId(),
                    t.getCreated().hasCommunity()?
                        t.getCreated().getCommunity().getId():
                        null,
                    true,
                    t.getVersion()
                );
                case UPDATED -> Tuple.of(
                    t.getEventId(),
                    t.getPostId(),
                    t.getUpdated().hasCommunityId()?
                        t.getUpdated().getCommunityId():
                        null,
                    t.getUpdated().getIsPublished(),
                    t.getVersion()
                );
                default -> throw new IllegalArgumentException("Invalid event case for post upsert!");
            })
            .toList();
    }
    
    private List<Tuple> getDeleteParams(List<PostEvent> toDelete){
        return toDelete.stream()
            .map(t -> Tuple.of(
                t.getEventId(),
                t.getPostId(),
                t.getVersion()
            ))
            .toList();
    }
}
