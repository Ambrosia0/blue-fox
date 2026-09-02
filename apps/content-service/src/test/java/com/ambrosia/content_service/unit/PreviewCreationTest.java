package com.ambrosia.content_service.unit;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.post.utils.TipTapPreviewConverter;
import com.ambrosia.content_service.post.utils.tiptap.TipTapDoc;
import com.ambrosia.content_service.util.PostTemplate;

import tools.jackson.databind.ObjectMapper;


public class PreviewCreationTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final PreviewConverter previewConverter = 
        new TipTapPreviewConverter(objectMapper, 50, 1);
    
    @Test
    void previewCreationTest(){
        var res = previewConverter.convert(PostTemplate.template);
        objectMapper.readValue(res, TipTapDoc.class);
    }
}
