package com.ambrosia.outbox.utils;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.outbox.entity.KafkaOutbox;

public class KafkaOutboxFactory {
    public static KafkaOutbox create(){
        return KafkaOutbox.from(
            "TestId",
            "test.topic",
            ByteBuffer.allocate(Long.BYTES)
                .putLong(ThreadLocalRandom.current().nextLong())
                .array()
        );
    }    
}
