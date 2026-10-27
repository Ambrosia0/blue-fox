package org.ambrosia.notification_service.event.api;

import jakarta.ws.rs.PathParam;
import org.ambrosia.notification_service.core.ActorFactory;
import org.ambrosia.notification_service.core.AppConfiguration;
import org.ambrosia.notification_service.core.policy.PolicyContext;
import org.ambrosia.notification_service.event.application.PostEventRegistry;
import org.ambrosia.notification_service.event.application.policy.PostNotificationPolicy;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

@Path("/api/v1/public")
public class PostEventController {
    private final AppConfiguration appConfiguration;
    
    private final PostEventRegistry postEventRegistry;
    
    @Inject
    Sse sse;

    @Inject
    ActorFactory actorFactory;
    
    public PostEventController(
            PostEventRegistry postEventRegistry,
            AppConfiguration appConfiguration
    ) {
        this.postEventRegistry = postEventRegistry;
        this.appConfiguration = appConfiguration;
    }
    
    @Path("/post/{postId}/events")
    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<OutboundSseEvent> consume(@PathParam("postId") Long postId){
        return Multi.createBy().merging().streams(
            ping(),
            postEventRegistry.register(postId, actorFactory.get())
                .map(message -> sse.newEvent("notification", message.body()))
        );
    }
    
    
    private Multi<OutboundSseEvent> ping(){
        return Multi.createFrom().ticks().every(appConfiguration.heartbitInterval())
            .onItem()
            .transform(tick -> sse.newEvent("ping", ""));
    }
}
