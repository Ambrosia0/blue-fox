package com.ambrosia.report_service.report.api.dto;

public record ReportReasonTranslation(
    Short reasonId,
    String lang,
    String title
) {}
