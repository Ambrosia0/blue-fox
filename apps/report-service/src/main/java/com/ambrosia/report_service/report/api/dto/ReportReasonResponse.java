package com.ambrosia.report_service.report.api.dto;

public record ReportReasonResponse(
    Short id,
    String title,
    String code,
    String lang
) {}
