package com.ambrosia.content_service.post.utils;

import org.springframework.stereotype.Component;

import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;

@Component
public class TextExtractor {
    private final ObjectMapper objectMapper;

    public TextExtractor(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    
    public String extractText(String doc){
        var builder = new StringBuilder();
        var parser = objectMapper.createParser(doc);
        while (parser.nextToken() != null) {
            var token = parser.currentToken();
            if(token == JsonToken.PROPERTY_NAME && parser.getValueAsString().equals("text"))
                if(parser.nextToken() == JsonToken.VALUE_STRING)
                    builder.append(parser.getValueAsString()).append(" ");
        }
        return builder.toString();
    }
}
