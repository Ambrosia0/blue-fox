package com.ambrosia.content_service.post.utils.tiptap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public record TipTapNode(
    @JsonProperty(required = true)
    TipTapExtension type,

    Map<String, Object> attrs,

    TipTapNode[] content,

    String text,
    
    TipTapMark[] marks

) {}
