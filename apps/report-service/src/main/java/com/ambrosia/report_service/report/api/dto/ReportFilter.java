package com.ambrosia.report_service.report.api.dto;

import org.springframework.data.domain.Sort.Direction;

import com.ambrosia.report_service.report.domain.entity.Report.Status;
import com.ambrosia.report_service.report.domain.entity.Report.TargetType;

import lombok.Builder;

@Builder 
public record ReportFilter(
    Status status, 
    TargetType targetType,
    Direction direction
) {}
