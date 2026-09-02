package org.ambrosia.notification_service.controller;

import java.time.Duration;

import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.ambrosia.notification_service.util.ActivityEventFactory;
import org.ambrosia.notification_service.util.AppConfiguration;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

@RolesAllowed({"user", "admin"})
@Path("/api/user/notification")
public class AsyncNotificationController {
    @Inject
    private Sse sse;

    @Inject
    private EventBus eventBus;

    @Inject
    private JsonWebToken token;

    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    @Channel("activity-notification")
    private Emitter<byte[]> activityEmitter;
    
    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<OutboundSseEvent> consume(@RestQuery Long postId){
        var id = token.getSubject();

        activityEmitter.send(ActivityEventFactory.create(id, ActivityEventType.CONNECT).toByteArray());

        // to receive post related events (comments related events, etc.)
        var topicChange = eventBus.<Long>consumer("user.control."+id)
            .toMulti()
            .map(Message::body)
            .onCompletion().ifEmpty().continueWith(postId);

        // ping to keep connection alive
        var keepAlive = Multi.createFrom().ticks()
            .every(appConfiguration.heartbitInterval())
            .onItem()
            .transform(tick -> sse.newEvent("ping", ""));

        // heartbeat events
        Multi<OutboundSseEvent> activityRefresh = Multi.createFrom().ticks()
            .every(Duration.ofMinutes(2))
            .invoke(() -> {
                var event = ActivityEventFactory.create(id, ActivityEventType.LEASE_REFRESH);
                activityEmitter.send(
                    org.eclipse.microprofile.reactive.messaging.Message.of(event.toByteArray())
                        .addMetadata(OutgoingKafkaRecordMetadata.builder()
                            .withKey(event.getUserId())
                            .build()
                        )
                );
                
            })
            .onItem()
            .transformToMultiAndMerge(t -> Multi.createFrom().empty());

        var dataMulti = topicChange
            .onItem()
            .transformToMultiAndMerge(newPostId ->{
                Multi<Message<String>> postMulti;
                if(newPostId != null)
                    postMulti = eventBus.<String>consumer("event.broadcast.post."+newPostId).toMulti()
                        .onTermination()
                        .invoke(() -> eventBus.consumer("event.broadcast.post."+newPostId).unregister());
                else
                    postMulti = Multi.createFrom().empty();
                var broadcast = eventBus.<String>consumer("event.broadcast").toMulti();
                var personal = eventBus.<String>consumer("event.user."+id).toMulti();
                return Multi.createBy().merging().streams(broadcast, personal, postMulti);
            })
            .map(message -> sse.newEvent("notification", message.body()))
            .onTermination().invoke(() ->{
                activityEmitter.send(ActivityEventFactory.create(id, ActivityEventType.DISCONNECT).toByteArray());
                eventBus.consumer("user.control."+id).unregister();
                eventBus.consumer("event.user."+id).unregister();
                eventBus.consumer("event.broadcast").unregister();
            });
        return Multi.createBy().merging().streams(dataMulti, keepAlive, activityRefresh);
    }

    @PATCH
    public void patchScope(@RestQuery Long postId){
        var id = token.claim("id").orElseThrow(() -> new RuntimeException("Error!"));
        eventBus.send("user.control."+id, postId);
    }
}
