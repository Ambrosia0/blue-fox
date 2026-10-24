package org.ambrosia.notification_service.notification.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.notification.api.dto.CommunityResponse;
import org.ambrosia.notification_service.notification.api.dto.NotificationFilter;
import org.ambrosia.notification_service.notification.api.dto.NotificationResponse;
import org.ambrosia.notification_service.notification.api.dto.UserResponse;
import org.ambrosia.notification_service.notification.application.query.NotificationQueryRepository;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
public class VertxNotificationQueryRepository implements NotificationQueryRepository {
    private final Pool pool;
    
    private final Function<Row, NotificationResponse> rowMapper = row -> new NotificationResponse(
        row.getString("content"),
        row.getUUID("targeted_id") != null?
            new UserResponse(
                row.getUUID("targeted_id"),
                row.getString("targeted_username"),
                row.getString("targeted_avatar_id")
            ):
            null,
        row.getLong("community_id") != null?
                new CommunityResponse(
                    row.getLong("community_id"),
                    row.getString("community_slug"),
                    row.getString("displayed_name"),
                    row.getString("community_avatar")
                ):
            null,
        row.getLocalDateTime("created_at").toInstant(ZoneOffset.UTC)
    );
    
    public VertxNotificationQueryRepository(Pool pool){
        this.pool = pool;
    }

    @Override
    public Uni<List<NotificationResponse>> getNotifications(NotificationFilter filter, UUID userId) {
        var sql = new StringBuilder("""
        WITH to_return AS (
            SELECT
                t.id,
                t.content,
                t.created_at,
                t.targeted_id,
                t.targeted_username,
                t.targeted_avatar_id,
                t.community_id,
                t.community_slug,
                t.community_name,
                t.community_avatar
            FROM (
                SELECT
                    tn.id,
                    tn.content,
                    NULL as targeted_id,
                    NULL as targeted_username,
                    NULL as targeted_avatar_id,
                    NULL::bigint as community_id,
                    NULL as community_slug,
                    NULL as community_name,
                    NULL as community_avatar,
                    tn.created_at
                FROM targeted_notification tn
                WHERE tn.user_id = $1
        
                UNION ALL
       
                SELECT
                    un.id,
                    un.content,
                    u.id as targeted_id,
                    u.username as targeted_username,
                    u.avatar_id as targeted_avatar_id,
                    NULL::bigint as community_id,
                    NULL as community_slug,
                    NULL as community_name,
                    NULL as community_avatar,
                    un.created_at
                FROM user_notification un
                JOIN service_user u ON u.id = un.user_id
                JOIN user_follow uf ON uf.user_id = $1
        
                UNION ALL
       
                SELECT
                    cn.id,
                    cn.content,
                    NULL as targeted_id,
                    NULL as targeted_username,
                    NULL as targeted_avatar_id,
                    c.id as community_id,
                    c.slug as community_slug,
                    c.displayed_name as community_name,
                    c.avatar_id as community_avatar,
                    cn.created_at
                FROM community_notification cn
                JOIN community c ON c.id = cn.community_id
                JOIN community_follow cf ON cf.user_id = $1
            ) t
            WHERE 1=1
        """
        );
        if(filter.lastSeenInstant() != null && filter.lastSeenId() != null)
            sql.append("AND (t.created_at, t.id) < ($2, $3) ");

        sql.append("ORDER BY t.created_at DESC, t.id DESC LIMIT 10");
        sql.append("""
        ),
        updated AS (
            UPDATE service_user
            SET notification_offset = CURRENT_TIMESTAMP
            WHERE id = $1
        )
        SELECT * FROM to_return
        """);
        return pool.withConnection(conn -> conn
                .preparedQuery(sql.toString())
                .execute(
                    filter.lastSeenInstant() != null && filter.lastSeenId() != null?
                        Tuple.of(userId, filter.lastSeenInstant(), filter.lastSeenId()):
                        Tuple.of(userId)
                )
                .onItem()
                .transform(rows -> {
                    var arr = new ArrayList<NotificationResponse>();
                    for(Row row: rows){
                        arr.add(rowMapper.apply(row));
                    }
                    return arr;
                })
            );
    }

    @Override
    public Uni<Long> countNew(UUID userId) {
        return pool.withConnection(conn -> conn.preparedQuery("""
        SELECT
            (
                SELECT COUNT(*)
                FROM targeted_notification tn
                WHERE tn.user_id = u.id
                AND tn.created_at > u.notification_offset
            ) +
            (
                SELECT COUNT(*)
                FROM user_notification un
                JOIN user_follow uf ON uf.user_id = u.id
                WHERE un.created_at > u.notification_offset
            ) +
            (
                SELECT COUNT(*)
                FROM community_notification cn
                JOIN community_follow cf ON cf.user_id = u.id
                WHERE cn.created_at > u.notification_offset
            )
        FROM service_user u
        WHERE u.id = $1
        """)
            .execute(Tuple.of(userId))
            .onItem()
            .transform(rows -> {
                var it = rows.iterator();
                return it.hasNext()?
                    it.next().getLong(0):
                    0L;
            })
        );
    }
}
