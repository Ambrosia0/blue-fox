package com.ambrosia.outbox.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ambrosia.outbox.kafka.KafkaOutboxConverter;
import com.ambrosia.outbox.kafka.KafkaOutboxRelay;
import com.ambrosia.outbox.kafka.KafkaOutboxService;
import com.ambrosia.outbox.repository.KafkaOutboxRepository;
import com.ambrosia.outbox.utils.KafkaOutboxConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(KafkaOutboxConfigurationProperties.class)
@EnableScheduling
public class KafkaOutboxAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(KafkaOutboxRepository.class)
    KafkaOutboxRepository kafkaOutboxRepository(JdbcAggregateOperations operations){
        var factory = new JdbcRepositoryFactory(operations);
        return factory.getRepository(KafkaOutboxRepository.class);
    }

    @Bean
    @ConditionalOnMissingBean(KafkaOutboxRelay.class)
    KafkaOutboxRelay kafkaOutboxRelay(
            KafkaOutboxRepository kafkaOutboxRepository, 
            KafkaTemplate<String, byte[]> kafkaTemplate
    ){
        return new KafkaOutboxRelay(kafkaOutboxRepository, kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(KafkaOutboxService.class)
    KafkaOutboxService kafkaOutboxService(
            KafkaOutboxRepository kafkaOutboxRepository,
            List<KafkaOutboxConverter<?>> converters
    ){
        return new KafkaOutboxService(kafkaOutboxRepository, converters);
    }
}
