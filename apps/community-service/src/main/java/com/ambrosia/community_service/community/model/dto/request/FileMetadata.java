package com.ambrosia.community_service.community.model.dto.request;

import com.ambrosia.community_service.community.utils.SupportedFileTypes;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FileMetadata(
    @NotNull @Min(1) @Max(4194304) Long fileSize,
    @NotNull SupportedFileTypes contentType,
    @NotNull @NotEmpty String md5
) {}
