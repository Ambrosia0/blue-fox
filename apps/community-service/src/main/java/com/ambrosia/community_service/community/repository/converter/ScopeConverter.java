package com.ambrosia.community_service.community.repository.converter;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.utils.ScopeEnum;

@Component 
@ReadingConverter 
public class ScopeConverter implements Converter<Array, Set<ScopeEnum>>{
    @Override
    public Set<ScopeEnum> convert(Array source) {
        try {
            return Arrays.stream(((Object[])source.getArray()))
                .map(t -> ScopeEnum.fromId(((Number) t).shortValue()))
                .collect(Collectors.toSet());
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to convert scope array!", e);
        }
    }
}
