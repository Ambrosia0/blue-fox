package org.ambrosia.notification_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.google.protobuf.util.JsonFormat;

import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostViewEvent {
    @Inject
    private EventBus eventBus;

    @Incoming("post-view-channel")
    public void postViewEvent(ConsumerRecords<Long, byte[]> records) throws Exception {
        for(var record: records){
            var body = AggregatedViewEvent.parseFrom(record.value());
            eventBus.publish("event.broadcast.post."+body.getPostId(), JsonFormat.printer().print(body));
        }
    }
}
