package com.ambrosia.content_service.unit;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.post.utils.TextExtractor;
import com.ambrosia.content_service.util.PostTemplate;

import tools.jackson.databind.ObjectMapper;

public class TextExtractTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TextExtractor textExtractor = new TextExtractor(objectMapper);
    
    @Test
    void textExtractText(){
        textExtractor.extractText(PostTemplate.template);
    }
}
