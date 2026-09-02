package com.ambrosia.report_service.report.model.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "report_reason")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportReason {
    @Id
    private Short id;

    @Column("code")
    private String code;

    @MappedCollection(idColumn = "report_reason_id")
    private Set<ReportReasonI18n> i18n;
}
