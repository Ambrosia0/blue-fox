package com.ambrosia.content_service.post.model.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.UniqueElements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PostEditRequest(
    String title,

    // @JsonDeserialize(using = PostContentDeserializer.class)
    @JsonProperty("post")
    String post,

    @Size(max = 3)
    @UniqueElements
    @JsonInclude(value = Include.NON_NULL)
    List<@NotBlank @Pattern(regexp = "#[\\p{L}\\p{N}_]{1,16}") String> tags,

    Long version
) {}
