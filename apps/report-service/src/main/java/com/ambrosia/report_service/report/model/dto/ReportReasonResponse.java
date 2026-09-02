package com.ambrosia.report_service.report.model.dto;

public record ReportReasonResponse(
    Short id,
    String title,
    String code,
    String lang
) {}
