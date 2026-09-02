package org.ambrosia.notification_service.controller;

import java.util.UUID;

import org.ambrosia.notification_service.util.AppConfiguration;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

@Path("/api/public/notification")
public class UnauthorizedAsyncNotificationController {
    @Inject
    private Sse sse;

    @Inject
    private EventBus eventBus;

    @Inject
    private AppConfiguration appConfiguration;

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<OutboundSseEvent> consume(@RestQuery Long postId){
        var generatedUuid = UUID.randomUUID();
        
        var topicChange = Multi.createBy().concatenating().streams(
            Multi.createFrom().item(postId),
            eventBus.<Long>consumer("anonymous."+generatedUuid)
                .toMulti()
                .map(Message::body)
        );
        var init = Uni.createFrom().item(sse.newEvent("init", generatedUuid.toString()));

        var keepAlive = Multi.createFrom().ticks().every(appConfiguration.heartbitInterval())
            .onItem()
            .transform(tick -> sse.newEvent("ping", ""));

        var dataMulti = topicChange.onItem().transformToMultiAndMerge(pId ->{
            Multi<Message<String>> postEvents;
            if(pId != null)
                postEvents = eventBus.<String>consumer("event.broadcast.post."+pId).toMulti()
                    .onTermination()
                    .invoke(() -> eventBus.consumer("event.broadcast.post."+pId).unregister());
            else
                postEvents = Multi.createFrom().empty();
            var broadcast = eventBus.<String>consumer("event.broadcast").toMulti();
            return Multi.createBy().merging().streams(broadcast, postEvents);
        })
        .map(message -> sse.newEvent("notification", message.body()))
        .onTermination().invoke(() ->{
            eventBus.consumer("anonymous."+generatedUuid).unregister();
            eventBus.consumer("event.broadcast").unregister();
        });
        return Multi.createBy().concatenating().streams(
            init.toMulti(), 
            Multi.createBy().merging().streams(dataMulti, keepAlive));
    }

    @PATCH
    public void patchScope(@RestQuery UUID userId ,@RestQuery Long postId){
        if(postId != null)
            eventBus.send("anonymous."+userId, postId);
        else
            eventBus.consumer("anonymous."+userId).unregister();
    }
}
