package com.ambrosia.outbox;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.ambrosia.outbox.kafka.KafkaOutboxRelay;
import com.ambrosia.outbox.repository.KafkaOutboxRepository;
import com.ambrosia.outbox.utils.KafkaOutboxFactory;

@SuppressWarnings("unchecked")
public class KafkaOutboxTests {
    KafkaOutboxRepository kafkaOutboxRepository = mock(KafkaOutboxRepository.class);
    KafkaTemplate<String, byte[]> kafkaTemplate = mock(KafkaTemplate.class);
    KafkaOutboxRelay ouboxWorker = new KafkaOutboxRelay(
        kafkaOutboxRepository,
        kafkaTemplate
    );

    CompletionException completionException = new CompletionException(new RuntimeException());

    @BeforeEach
    void init(){
        when(kafkaOutboxRepository.findUnclaimed(anyLong())).thenReturn(
            List.of(
                KafkaOutboxFactory.create(),
                KafkaOutboxFactory.create()
            )
        );
    }

    @Test
    void shouldFindUnclaimedThenDelete(){
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        ouboxWorker.flush();
        verify(kafkaOutboxRepository).findUnclaimed(anyLong());
    }

    @Test
    void shouldCallRecoverClaimOnKafkaFailure(){
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenReturn(CompletableFuture.failedFuture(completionException));
        ouboxWorker.flush();
        verify(kafkaOutboxRepository).recoverClaim(anyList());
    }

    @Test
    void shouldThrowRuntimeExceptionOnDatabaseFailure(){
        doThrow(new RuntimeException())
            .when(kafkaOutboxRepository)
            .deleteByIds(anyIterable());
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        assertThrows(
            RuntimeException.class,
            () -> ouboxWorker.flush()
        );
    }
}
