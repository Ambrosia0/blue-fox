package com.ambrosia.profile_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    @Bean
    DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, byte[]> template){
        return new DeadLetterPublishingRecoverer(template);
    }

    @Bean
    DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer){
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 2L));
    }
    
    @Bean
    KafkaListenerErrorHandler serializationErrorHandler(){
        return (message, exception) ->{
            return exception;
        };
    }
}
