package com.ambrosia.outbox.elastic;

import com.ambrosia.outbox.entity.SearchIndexOutbox;

/**
 * Converter for objects to outbox entity
 * @param <T> type of converted object
 */
public interface SearchIndexOutboxConverter<T>{
    /**
     * Returns source type handled by this converter
     * <p> Preferable to obtaining type, as spring beans can be proxied </p>
     * @return source object type
     */
    Class<T> getSourceType();
    SearchIndexOutbox convert(Object source);
}
