package com.ambrosia.content_service.post.model.dto.response;

import java.io.Serializable;

public record PostResponse(
    long id,
    String title
) implements Serializable{}
