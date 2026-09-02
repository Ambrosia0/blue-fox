package com.ambrosia.profile_service.kafka;

import java.time.Duration;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

import com.ambrosia.profile_service.kafka.utils.PresenseState;


public class ActivityIdleProcessor implements Processor<UUID, ActivityEvent, UUID, ActivityEvent>{
    private ProcessorContext<UUID, ActivityEvent> context;
    private KeyValueStore<UUID, PresenseState> activityStore;
    
    @Override
    public void init(ProcessorContext<UUID, ActivityEvent> context) {
        this.context = context;
        this.activityStore = context.getStateStore("user-activity-store");
        context.schedule(
            Duration.ofMinutes(2),
            PunctuationType.WALL_CLOCK_TIME,
            timestamp -> {
                // if notification service is down and there is no hearbeat-events, deletes old records
                try (var all = activityStore.all()) { // summary difficulty O(N/partitions)
                    while(all.hasNext()){
                        var cur = all.next();
                        if(cur.value.timestamp + Duration.ofMinutes(2).toMillis() >= timestamp){
                            activityStore.delete(cur.key);
                            context.forward(new Record<UUID, ActivityEvent>(
                                    cur.key,
                                    ActivityEvent.newBuilder()
                                        .setUserId(cur.key.toString())
                                        .setType(ActivityEventType.DISCONNECT)
                                        .setTimestamp(cur.value.timestamp)
                                        .build(),
                                    timestamp
                                )
                            );
                        }
                    }
                }
            }
        );
    }

    @Override
    public void process(Record<UUID, ActivityEvent> record) {
        var val = activityStore.get(record.key());
        var delta = switch(record.value().getType()){
                case CONNECT -> 1;
                case DISCONNECT -> -1;
                default -> 0;
            };
        if(val == null){
            val = new PresenseState();
        }
        val.delta += delta;
        val.timestamp = record.value().getTimestamp();
        activityStore.put(record.key(), val);
        
        // if user is disconnected (doesn't have sse-connection to notification - service(connection_delta == 0))
        if(val.delta == 0){
            activityStore.delete(record.key());
            context.forward(
                record.withValue(ActivityEvent.newBuilder()
                    .setUserId(record.key().toString())
                    .setTimestamp(record.timestamp())
                    .setType(ActivityEventType.DISCONNECT)
                    .build()
                )
            );
        } else if(val.delta == 1){ // if user is connected first time (connection_delta == 1)
            context.forward(
                record.withValue(ActivityEvent.newBuilder()
                    .setUserId(record.key().toString())
                    .setType(ActivityEventType.CONNECT)
                    .setTimestamp(val.timestamp)
                    .build()
                )
            );
        }
    }
}
