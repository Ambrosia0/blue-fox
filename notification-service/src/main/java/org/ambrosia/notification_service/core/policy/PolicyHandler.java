package org.ambrosia.notification_service.core.policy;

import io.smallrye.mutiny.Uni;

public interface PolicyHandler<P extends Policy<? extends PolicyData>, A>{
    Uni<Void> validate(Actor actor, A arg);
    Class<P> getType();
}
