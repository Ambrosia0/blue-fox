package com.ambrosia.report_service.report.model.dto;

import com.ambrosia.report_service.report.model.entity.Report.TargetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReportRequest(
    Short reportReasonId,
    TargetType targetType,
    @Size(max = 300) String reportContent,
    @NotBlank @Size(max = 36) String reportContentKey
) {}
