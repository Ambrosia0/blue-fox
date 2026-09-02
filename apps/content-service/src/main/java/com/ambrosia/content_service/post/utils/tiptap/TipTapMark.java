package com.ambrosia.content_service.post.utils.tiptap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipTapMark(
    String type,
    Map<String, Object> attrs
) {}
