package org.ambrosia.notification_service.projections.infrastructure.persistence.impl;

import com.ambrosia.community_service.kafka_events.CommunityBan;
import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.ambrosia.community_service.kafka_events.CommunityUnban;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.CommunityBanRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class VertxCommunityBanRepository implements CommunityBanRepository {
    private final Pool pool;
    
    private final String INSERT_SQL = """
    INSERT INTO community_ban(user_id, community_id, before_date)
    VALUES ($1, $2, $3)
    """;
    
    private final String DELETE_SQL = """
    DELETE FROM community_ban
    WHERE user_id = $1
    AND community_id = $2
    AND before_date > CURRENT_TIMESTAMP
    """;
    
    public VertxCommunityBanRepository(Pool pool) {
        this.pool = pool;
    }
    
    @Override
    public Uni<Void> process(List<CommunityBanEvent> toInsert, List<CommunityBanEvent> toDelete) {
        return pool.withTransaction(conn -> insert(conn, toInsert)
            .chain(() -> delete(conn, toDelete))
        );
    }
    
    private Uni<Void> insert(SqlConnection sqlConnection, List<CommunityBanEvent> events){
        if(events == null || events.isEmpty())
            return Uni.createFrom().voidItem();
        return sqlConnection.preparedQuery(INSERT_SQL)
            .executeBatch(getInsertParams(events))
            .replaceWithVoid();
    }
    
    private Uni<Void> delete(SqlConnection sqlConnection, List<CommunityBanEvent> events){
        if(events == null || events.isEmpty())
            return Uni.createFrom().voidItem();
        return sqlConnection.preparedQuery(DELETE_SQL)
            .executeBatch(getDeleteParams(events))
            .replaceWithVoid();
    }
    
    private List<Tuple> getInsertParams(List<CommunityBanEvent> toInsert){
        return toInsert.stream()
            .map(t -> Tuple.of(
                t.getBan().getUserId(),
                t.getBan().getCommunityId(),
                Instant.ofEpochMilli(t.getBan().getBeforeDate())
                    .atOffset(ZoneOffset.UTC)
            ))
            .toList();
    }
    
    private List<Tuple> getDeleteParams(List<CommunityBanEvent> toDelete){
        return toDelete.stream()
            .map(t -> Tuple.of(
                t.getUnban().getUserId(),
                t.getUnban().getCommunityId()
            ))
            .toList();
    }
}
