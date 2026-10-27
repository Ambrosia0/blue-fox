package org.ambrosia.notification_service.event.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.event.domain.UserFollowRepository;
import org.ambrosia.notification_service.event.infrastructure.entity.UserFollow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
public class VertxUserFollowRepository implements UserFollowRepository {
    private final Pool pool;
    
    private final Function<Row, UserFollow> followMapper = row -> new UserFollow(
        row.getUUID("followed_user_id"),
        row.getLong("community_id")
    );
    
    public VertxUserFollowRepository(Pool pool) {
        this.pool = pool;
    }
    
    public Uni<List<UserFollow>> getFollows(UUID userId){
        return pool.withConnection(conn -> conn
            .preparedQuery("""
            SELECT t.*
            FROM (
                SELECT
                    community_id,
                    NULL::uuid as followed_user_id
                FROM community_follow
                WHERE user_id = $1

                UNION ALL

                SELECT
                    NULL::bigint as community_id,
                    followed_user_id
                FROM user_follow
                WHERE user_id = $1
            ) t
            """)
            .execute(Tuple.of(userId))
            .onItem()
            .transform(rowSet -> {
                var arr = new ArrayList<UserFollow>();
                for(Row follow: rowSet){
                    arr.add(followMapper.apply(follow));
                }
                return arr;
            })
        );
    }
}
