package com.ambrosia.report_service.report.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.report_service.report.model.entity.key.ReportReasonI18nKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "report_reason_i18n")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportReasonI18n implements Persistable<ReportReasonI18nKey>{
    @Id
    private ReportReasonI18nKey id;

    @Column(value = "title")
    private String title;

    @Builder.Default
    @Transient
    private boolean isNew = false;

    public static ReportReasonI18n create(Short reasonId, String lang, String title){
        return new ReportReasonI18n(new ReportReasonI18nKey(reasonId, lang), title, true);
    }
}
