package com.ambrosia.report_service.report.model.dto.admin;

import java.time.Instant;
import java.util.UUID;

import com.ambrosia.report_service.report.model.entity.Report.Status;
import com.ambrosia.report_service.report.model.entity.Report.TargetType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record ReportResponse(
    UUID id,
    Long reportReasonId,
    String reportContent,
    TargetType targetType,
    Status status,
    String reportedContentKey,
    
    @JsonInclude(value = Include.NON_NULL)
    UUID resolvedBy,
    
    Instant createdAt
) {}
