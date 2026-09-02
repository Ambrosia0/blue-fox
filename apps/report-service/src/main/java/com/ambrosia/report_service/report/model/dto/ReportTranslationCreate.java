package com.ambrosia.report_service.report.model.dto;

import com.ambrosia.report_service.report.utils.Iso6391;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportTranslationCreate(
    @NotNull Short reasonId,
    @NotBlank @Size(max = 100) String title,
    @Iso6391 String lang
) {}
