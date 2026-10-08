package com.ambrosia.content_service.post.api.dto.request;

import java.time.LocalDate;

import org.springframework.data.domain.Sort.Direction;

public record PostSearch(
    String searchString,
    LocalDate date,
    Direction direction
) {}
