package org.ambrosia.notification_service.event.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.ambrosia.notification_service.event.application.UserActivityRegistry;
import org.ambrosia.notification_service.event.application.UserEventRegistry;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.ambrosia.notification_service.notification.util.ActivityEventFactory;
import org.ambrosia.notification_service.core.AppConfiguration;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

@Authenticated
@Path("/api/v1")
public class EventController {
    private final AppConfiguration appConfiguration;
    
    private final UserEventRegistry userEventRegistry;
    
    private final UserActivityRegistry userActivityRegistry;
    
    @Inject
    Sse sse;
    
    @Inject
    JsonWebToken jwt;
    
    public EventController(
            AppConfiguration appConfiguration,
            UserEventRegistry userEventRegistry,
            UserActivityRegistry userActivityRegistry
    ) {
        this.appConfiguration = appConfiguration;
        this.userEventRegistry = userEventRegistry;
        this.userActivityRegistry = userActivityRegistry;
    }
    
    @Path("/events")
    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<OutboundSseEvent> userEvents(){
        var id = UUID.fromString(jwt.getSubject());
        return Multi.createBy().merging().streams(
            ping(),
            userEventRegistry.register(id)
                .map(message -> sse.newEvent("notification", message.body())),
            userActivityRegistry.register(id)
        );
    }
    
    // ping to keep connection alive
    private Multi<OutboundSseEvent> ping(){
        return Multi.createFrom().ticks()
            .every(appConfiguration.heartbitInterval())
            .onItem().transform(tick -> sse.newEvent("ping", ""));
    }
}
