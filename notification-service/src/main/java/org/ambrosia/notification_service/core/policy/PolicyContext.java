package org.ambrosia.notification_service.core.policy;

import io.smallrye.mutiny.Uni;

public interface PolicyContext{
    Uni<Void> validate(Actor actor, Class<? extends Policy<?>> policy, Object arg);
}
