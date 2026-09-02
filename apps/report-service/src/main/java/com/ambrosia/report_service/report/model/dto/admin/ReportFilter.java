package com.ambrosia.report_service.report.model.dto.admin;

import org.springframework.data.domain.Sort.Direction;

import com.ambrosia.report_service.report.model.entity.Report.Status;
import com.ambrosia.report_service.report.model.entity.Report.TargetType;

public record ReportFilter(
    Status status, 
    TargetType targetType,
    Direction direction
) {}
