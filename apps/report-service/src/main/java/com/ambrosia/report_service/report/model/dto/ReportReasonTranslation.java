package com.ambrosia.report_service.report.model.dto;

public record ReportReasonTranslation(
    Short reasonId,
    String lang,
    String title
) {}
