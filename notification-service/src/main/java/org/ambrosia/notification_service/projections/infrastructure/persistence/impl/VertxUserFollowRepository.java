package org.ambrosia.notification_service.projections.infrastructure.persistence.impl;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.UserFollowKey;
import org.ambrosia.notification_service.projections.infrastructure.persistence.UserFollowRepository;

import java.util.List;

@ApplicationScoped
public class VertxUserFollowRepository implements UserFollowRepository {
    private final Pool pool;
    
    private final String INSERT_SQL = "INSERT INTO user_follow(user_id, followed_user_id) VALUES ($1, $2)";
    
    private final String DELETE_SQL = "DELETE FROM user_follow WHERE user_id = $1 AND followed_user_id = $2";

    public VertxUserFollowRepository(Pool pool){
        this.pool = pool;
    }
    
    public Uni<Void> applyChanges(List<UserFollowKey> toSave, List<UserFollowKey> toDelete){
        return pool.withConnection(conn -> insert(conn, toSave)
            .chain(() -> delete(conn, toDelete))
        );
    }
    
    private Uni<Void> insert(SqlConnection sqlConnection, List<UserFollowKey> toSave){
        if(toSave == null || toSave.isEmpty())
            return Uni.createFrom().voidItem();
        return sqlConnection.preparedQuery(INSERT_SQL)
            .executeBatch(toSave.stream()
                .map(t -> Tuple.of(
                    t.userId(),
                    t.followedUserId())
                )
                .toList()
            )
            .replaceWithVoid();
    }
    
    private Uni<Void> delete(SqlConnection sqlConnection, List<UserFollowKey> toDelete){
        if(toDelete == null | toDelete.isEmpty())
            return Uni.createFrom().voidItem();
        return sqlConnection.preparedQuery(DELETE_SQL)
            .executeBatch(toDelete.stream()
                .map(t -> Tuple.of(
                        t.userId(),
                        t.followedUserId()
                    )
                )
                .toList()
            )
            .replaceWithVoid();
    }
}
