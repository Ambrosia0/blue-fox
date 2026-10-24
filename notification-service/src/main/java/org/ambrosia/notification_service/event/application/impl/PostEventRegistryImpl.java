package org.ambrosia.notification_service.event.application.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.core.policy.PolicyContext;
import org.ambrosia.notification_service.event.application.PostEventRegistry;
import org.ambrosia.notification_service.event.application.policy.PostNotificationPolicy;

@ApplicationScoped
public class PostEventRegistryImpl implements PostEventRegistry {
    @Inject
    EventBus eventBus;
    
    private final PolicyContext policyContext;
    
    public PostEventRegistryImpl(PolicyContext policyContext) {
        this.policyContext = policyContext;
    }
    
    @Override
    public Multi<Message<String>> register(Long postId, Actor actor) {
        var consumer = eventBus.<String>consumer("event.broadcast.post."+postId);
        return consumer.toMulti()
            .onSubscription().call(() -> policyContext.validate(actor, PostNotificationPolicy.class, postId))
            .onTermination().call(consumer::unregister);
    }
}
