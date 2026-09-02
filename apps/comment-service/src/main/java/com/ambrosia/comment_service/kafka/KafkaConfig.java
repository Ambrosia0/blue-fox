package com.ambrosia.comment_service.kafka;

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
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, Object> template){
        return new DeadLetterPublishingRecoverer(template);
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer){
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 2L));
    }
    
    @Bean
    public KafkaListenerErrorHandler serializationErrorHandler(){
        return (message, exception) ->{
            return exception;
        };
    }

    // @Bean
    // public RecordMessageConverter messageConverter(){
    //     return new StringJacksonJsonMessageConverter();
    // }
}
