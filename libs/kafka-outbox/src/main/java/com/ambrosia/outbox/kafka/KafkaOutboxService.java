package com.ambrosia.outbox.kafka;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.ambrosia.outbox.OutboxService;
import com.ambrosia.outbox.repository.KafkaOutboxRepository;

import jakarta.validation.constraints.NotNull;

public class KafkaOutboxService implements OutboxService{
    protected final KafkaOutboxRepository kafkaOutboxRepository;

    protected final Map<Class<?>, KafkaOutboxConverter<?>> converters;

    public KafkaOutboxService(
        KafkaOutboxRepository kafkaOutboxRepository,
        List<KafkaOutboxConverter<?>> converters
    ){
        this.kafkaOutboxRepository = kafkaOutboxRepository;
        this.converters = converters.stream()
            .collect(
                Collectors.toUnmodifiableMap(KafkaOutboxConverter::getSourceType, Function.identity())
            );
    }

    public void put(@NotNull Object event) {
        Assert.notNull(event, "Event must not be null!");
        var converter = converters.get(event.getClass());
        if(converter == null)
            throw new RuntimeException("Converter for class "+event.getClass().toString()+" doesn't exist!");
        kafkaOutboxRepository.save(converter.convert(event));
    }
}
