package com.ambrosia.report_service.report.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.report_service.report.model.dto.ReportReasonTranslation;
import com.ambrosia.report_service.report.model.dto.ReportTranslationCreate;
import com.ambrosia.report_service.report.model.dto.admin.ReportFilter;
import com.ambrosia.report_service.report.model.dto.admin.ReportResponse;
import com.ambrosia.report_service.report.model.entity.ReportReason;

public interface AdminReportService {
    Slice<ReportResponse> getReports(ReportFilter reportFilter, Pageable pageable);
    ReportReasonTranslation createTranslation(ReportTranslationCreate reasonCreate);
    void deleteTranslation(Short reasonId, String lang);
    List<ReportReasonTranslation> getTranslations(Short reasonId);
    void closeReport(UUID reportId, UUID requestingAdmin);
    List<ReportReason> getReasons();
}
