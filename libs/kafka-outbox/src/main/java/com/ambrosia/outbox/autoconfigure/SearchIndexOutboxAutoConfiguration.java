package com.ambrosia.outbox.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ambrosia.outbox.elastic.ElasticsearchOutboxHandler;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;
import com.ambrosia.outbox.elastic.SearchIndexOutboxConverter;
import com.ambrosia.outbox.elastic.SearchIndexOutboxService;
import com.ambrosia.outbox.repository.SearchIndexOutboxRepository;
import com.ambrosia.outbox.utils.SearchIndexOutboxConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(SearchIndexOutboxConfigurationProperties.class)
@EnableScheduling
public class SearchIndexOutboxAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(SearchIndexOutboxRepository.class)
    SearchIndexOutboxRepository searchIndexOutboxRepository(JdbcAggregateOperations operations){
        var factory = new JdbcRepositoryFactory(operations);
        return factory.getRepository(SearchIndexOutboxRepository.class);
    }

    @Bean
    @ConditionalOnMissingBean(ElasticsearchOutboxRelay.class)
    ElasticsearchOutboxRelay elasticsearchOutboxRelay(
            SearchIndexOutboxRepository searchIndexOutboxRepository,
            List<ElasticsearchOutboxHandler<?>> handlers
    ){
        return new ElasticsearchOutboxRelay(handlers, searchIndexOutboxRepository);
    }

    @Bean
    @ConditionalOnMissingBean(SearchIndexOutboxService.class)
    SearchIndexOutboxService searchIndexOutboxService(
            SearchIndexOutboxRepository searchIndexOutboxRepository,
            List<SearchIndexOutboxConverter<?>> converters
    ){
        return new SearchIndexOutboxService(searchIndexOutboxRepository, converters);
    }
}
