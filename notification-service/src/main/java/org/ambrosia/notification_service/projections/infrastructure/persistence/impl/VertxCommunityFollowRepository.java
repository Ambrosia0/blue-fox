package org.ambrosia.notification_service.projections.infrastructure.persistence.impl;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.CommunityFollowKey;
import org.ambrosia.notification_service.projections.infrastructure.persistence.CommunityFollowRepository;

import java.util.List;

@ApplicationScoped
public class VertxCommunityFollowRepository implements CommunityFollowRepository {
    private final Pool pool;
    
    private final String INSERT_SQL = "INSERT INTO community_follow(user_id, community_id) VALUES ($1, $2)";
    
    private final String DELETE_SQL = "DELETE FROM community_follow WHERE user_id = $1 AND community_id = $2";

    public VertxCommunityFollowRepository(Pool pool){
        this.pool = pool;
    }
    
    public Uni<Void> process(List<CommunityFollowKey> toSave, List<CommunityFollowKey> toDelete){
        return pool.withTransaction(conn -> insert(conn, toSave)
            .chain(() -> delete(conn, toDelete))
        );
    }
    
    private Uni<Void> insert(SqlConnection connection, List<CommunityFollowKey> events){
        if(events.isEmpty())
            return Uni.createFrom().voidItem();
        return connection.preparedQuery(INSERT_SQL)
            .executeBatch(events.stream()
                .map(t -> Tuple.of(
                    t.userId(),
                    t.communityId())
                )
                .toList())
            .replaceWithVoid();
    }
    
    private Uni<Void> delete(SqlConnection connection, List<CommunityFollowKey> events){
        if(events.isEmpty())
            return Uni.createFrom().voidItem();
        return connection.preparedQuery(DELETE_SQL)
            .executeBatch(events.stream()
                .map(t -> Tuple.of(
                    t.userId(),
                    t.communityId()
                ))
                .toList())
            .replaceWithVoid();
    }
}
