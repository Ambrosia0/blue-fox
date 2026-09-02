package com.ambrosia.profile_service.core;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class InstantConverter implements Converter<Timestamp, Instant>{
    @Override
    public Instant convert(Timestamp source) {
        return source.toInstant();
    }
}
