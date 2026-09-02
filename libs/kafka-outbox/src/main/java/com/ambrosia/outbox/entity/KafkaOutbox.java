package com.ambrosia.outbox.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.github.robsonkades.uuidv7.UUIDv7;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kafka_outbox")
public class KafkaOutbox implements Persistable<UUID>{
    @Id
    private UUID id;
    
    @Column("kafka_id")
    private String kafkaId;

    @Column("topic")
    private String topic;

    @Column("payload")
    private byte[] payload;

    @Transient
    private boolean isNew = true;

    public static KafkaOutbox from(String key, String topic, byte[] payload){
        return new KafkaOutbox(
            UUIDv7.randomUUID(), 
            key,
            topic, 
            payload,
            true
        );
    }
}
