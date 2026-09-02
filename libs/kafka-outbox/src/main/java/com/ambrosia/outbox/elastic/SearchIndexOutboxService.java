package com.ambrosia.outbox.elastic;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.ambrosia.outbox.OutboxService;
import com.ambrosia.outbox.repository.SearchIndexOutboxRepository;

import jakarta.validation.constraints.NotNull;

public class SearchIndexOutboxService implements OutboxService{
    protected final SearchIndexOutboxRepository searchIndexOutboxRepository;
    protected final Map<Class<?>, SearchIndexOutboxConverter<?>> converters;

    public SearchIndexOutboxService(
        SearchIndexOutboxRepository searchIndexOutboxRepository,
        List<SearchIndexOutboxConverter<?>> converters
    ){
        this.searchIndexOutboxRepository = searchIndexOutboxRepository;
        this.converters = converters
            .stream()
            .collect(Collectors.toUnmodifiableMap(
                SearchIndexOutboxConverter::getSourceType, 
                Function.identity())
            );
    }

    public void put(@NotNull Object obj){
        Assert.notNull(obj, "Object must not be null!");
        var converter = converters.get(obj.getClass());
        if(converter == null)
            throw new RuntimeException("Converter for type "+obj.getClass().toString()+" doesn't exist!");
        searchIndexOutboxRepository.save(converter.convert(obj));
    }
}
