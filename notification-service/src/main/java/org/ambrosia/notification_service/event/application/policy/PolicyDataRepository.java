package org.ambrosia.notification_service.event.application.policy;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nullable;
import org.ambrosia.notification_service.event.domain.policy.PostNotificationPolicyData;

import java.util.Optional;
import java.util.UUID;

public interface PolicyDataRepository {
    Uni<Optional<PostNotificationPolicyData>> loadForNotification(@Nullable UUID userId, Long postId);
}
