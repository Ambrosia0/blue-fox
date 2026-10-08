package com.ambrosia.report_service.report.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.report_service.report.api.dto.ReportFilter;
import com.ambrosia.report_service.report.api.dto.ReportReasonTranslation;
import com.ambrosia.report_service.report.api.dto.ReportResponse;
import com.ambrosia.report_service.report.api.dto.ReportTranslationCreate;
import com.ambrosia.report_service.report.domain.entity.ReportReason;

public interface AdminReportService {
    ReportReasonTranslation createTranslation(ReportTranslationCreate reasonCreate);
    void deleteTranslation(Short reasonId, String lang);
    List<ReportReasonTranslation> getTranslations(Short reasonId);
    void closeReport(UUID reportId, UUID requestingAdmin);
    List<ReportReason> getReasons();
}
