package com.ambrosia.content_service.post.utils;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.core.PostValidator;
import com.ambrosia.content_service.post.utils.tiptap.TipTapDoc;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class TipTapPostValidator implements PostValidator{
    private final ObjectMapper objectMapper;

    @Override
    public boolean isValid(String content) {
        try {
            objectMapper.treeToValue(
                objectMapper.readTree(content), 
                TipTapDoc.class
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
