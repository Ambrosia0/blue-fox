package com.ambrosia.content_service.post.utils.tiptap;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TipTapDoc(
    @JsonProperty(required = true)
    DocType type,

    @JsonProperty(required = true)
    TipTapNode[] content
) {}
