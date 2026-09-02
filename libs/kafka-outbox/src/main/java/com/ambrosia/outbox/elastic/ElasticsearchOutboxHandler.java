package com.ambrosia.outbox.elastic;

import java.util.List;
/**
 * Handler used to process outboxed entities
 * @param <T> type of stored entities in outbox
 */
public interface ElasticsearchOutboxHandler<T> extends SearchIndexOutboxConverter<T>{
    /**
     * Unique name for grouping entities in database
     * @return
     */
    String getName();

    /**
     * Processing of stored entities
     * @param entities processed database entities
     */
    void process(List<String> entities);
}
