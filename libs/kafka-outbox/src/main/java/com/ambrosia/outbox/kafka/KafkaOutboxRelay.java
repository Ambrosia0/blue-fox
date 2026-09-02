package com.ambrosia.outbox.kafka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.ambrosia.outbox.repository.KafkaOutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaOutboxRelay {
    private final KafkaOutboxRepository kafkaOutboxRepository;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.kafka.poll-interval:5000}")
    public void flush(){
        var toSend = kafkaOutboxRepository.findUnclaimed(100L);
        if(toSend.isEmpty())
            return;

        var ids = toSend.stream().map(t -> t.getId()).toList();
        try {

            var result = toSend.stream().map(t -> 
                kafkaTemplate.send(
                    t.getTopic(),
                    t.getKafkaId(),
                    t.getPayload()
                )
            )
            .toList();

            CompletableFuture.allOf(
                result.toArray(CompletableFuture[]::new)
            ).join();
            kafkaOutboxRepository.deleteByIds(ids);
        } catch (CompletionException e) {
            kafkaOutboxRepository.recoverClaim(ids);
        } catch (RuntimeException e){
            log.error("Can't flush outbox!", e);
            throw e;
        }
    }
}
