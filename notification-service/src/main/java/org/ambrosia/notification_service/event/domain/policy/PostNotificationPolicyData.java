package org.ambrosia.notification_service.event.domain.policy;

import org.ambrosia.notification_service.core.policy.PolicyData;

public record PostNotificationPolicyData(
    boolean isModerator,
    boolean isFollowed,
    boolean isBanned,
    boolean isPrivate
) implements PolicyData {}
