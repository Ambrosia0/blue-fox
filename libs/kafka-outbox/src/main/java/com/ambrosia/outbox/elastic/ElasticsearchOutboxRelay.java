package com.ambrosia.outbox.elastic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ambrosia.outbox.AbstractOutboxRelay;
import com.ambrosia.outbox.entity.SearchIndexOutbox;
import com.ambrosia.outbox.repository.SearchIndexOutboxRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Universal relay for elasticsearch outbox
 * @param <T> object type of stored items
 */
@Slf4j
public class ElasticsearchOutboxRelay extends AbstractOutboxRelay<SearchIndexOutbox>{
    private final Map<String, ElasticsearchOutboxHandler<?>> handlers;

    private final SearchIndexOutboxRepository searchIndexOutboxRepository;

    public ElasticsearchOutboxRelay(
            List<ElasticsearchOutboxHandler<?>> handlers,
            SearchIndexOutboxRepository searchIndexOutboxRepository
        ){
        this.handlers = handlers.stream()
            .collect(
                Collectors.toUnmodifiableMap(k -> k.getName(), v -> v)
            );
        this.searchIndexOutboxRepository = searchIndexOutboxRepository;
    }

    @Override
    protected List<SearchIndexOutbox> fetchBatch() {
        log.debug("Fetching batch!");
        return searchIndexOutboxRepository.findUnclaimed(100L);
    }

    @Override
    protected void processBatch(List<SearchIndexOutbox> batch) {
        if(batch == null || batch.isEmpty())
            return;
        log.debug("Processing batch!");
        var ids = batch.stream().map(t -> t.getId()).toList();
        try {
            batch
                .stream()
                .collect(Collectors.groupingBy(SearchIndexOutbox::getEntityType))
                .forEach((type, entities) -> {
                    var handler = handlers.get(type);
                    handler.process(entities.stream().map(t -> t.getPayload()).toList());
                });
            searchIndexOutboxRepository.deleteByIds(ids);
        } catch (RuntimeException e) {
            log.error("Can't process elasticsearch outbox batch!", e);
            searchIndexOutboxRepository.recoverClaim(ids);
            throw e;
        }
    }
}
