package org.ambrosia.notification_service.event.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.event.application.policy.PolicyDataRepository;
import org.ambrosia.notification_service.event.domain.policy.PostNotificationPolicyData;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
public class VertxPolicyDataRepository implements PolicyDataRepository {
    private final Pool pool;
    
    private final Function<Row, PostNotificationPolicyData> mapper = row ->
        new PostNotificationPolicyData(
            row.getBoolean("is_moderator"),
            row.getBoolean("is_followed"),
            row.getBoolean("is_banned"),
            row.getBoolean("is_private")
        );
    
    public VertxPolicyDataRepository(Pool pool) {
        this.pool = pool;
    }
    
    @Override
    public Uni<Optional<PostNotificationPolicyData>> loadForNotification(UUID userId, Long postId) {
        return pool
            .withConnection(conn -> conn
                .preparedQuery("""
                    SELECT
                        EXISTS(
                            SELECT 1 FROM community_permission
                            WHERE community_id = c.id
                            AND user_id = $2
                        ) as is_moderator,
                        EXISTS(
                            SELECT 1 FROM community_follow
                            WHERE community_id = c.id
                            AND user_id = $2
                        ) as is_followed,
                        EXISTS(
                            SELECT 1 FROM community_ban
                            WHERE community_id = c.id
                            AND user_id = $2
                        ) as is_banned,
                        c.is_private
                    FROM post p
                    LEFT JOIN community c ON c.id = p.community_id
                    WHERE p.id = $1
                    AND p.is_published = 'true'
                    """)
                .execute(Tuple.of(postId, userId))
                .onItem()
                .transform(rows -> {
                    var it = rows.stream().iterator();
                    if(it.hasNext())
                        return Optional.of(mapper.apply(it.next()));
                    return Optional.empty();
                })
            );
    }
}
