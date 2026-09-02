package com.ambrosia.report_service.report.model.entity.key;

import org.springframework.data.relational.core.mapping.Column;

public record ReportReasonI18nKey(
    @Column("report_reason_id") Short reasonId,
    @Column("lang") String lang
) {
    public static ReportReasonI18nKey create(Short reasonId, String lang){
        return new ReportReasonI18nKey(reasonId, lang);
    }
}
