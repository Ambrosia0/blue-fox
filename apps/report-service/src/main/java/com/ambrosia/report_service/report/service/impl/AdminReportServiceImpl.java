package com.ambrosia.report_service.report.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.report_service.exception.report.ReportAlreadyClosedException;
import com.ambrosia.report_service.exception.report.ReportDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportReasonDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTranslationDoesntExistException;
import com.ambrosia.report_service.report.model.dto.ReportReasonTranslation;
import com.ambrosia.report_service.report.model.dto.ReportTranslationCreate;
import com.ambrosia.report_service.report.model.dto.admin.ReportFilter;
import com.ambrosia.report_service.report.model.dto.admin.ReportResponse;
import com.ambrosia.report_service.report.model.entity.ReportReason;
import com.ambrosia.report_service.report.model.entity.Report.Status;
import com.ambrosia.report_service.report.repository.ReportQueryRepository;
import com.ambrosia.report_service.report.repository.ReportReasonI18nRepository;
import com.ambrosia.report_service.report.repository.ReportReasonRepository;
import com.ambrosia.report_service.report.repository.ReportRepository;
import com.ambrosia.report_service.report.service.AdminReportService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminReportServiceImpl implements AdminReportService{
    private final ReportRepository reportRepository;

    private final ReportQueryRepository reportQueryRepository;

    private final ReportReasonI18nRepository reportReasonI18nRepository;

    private final ReportReasonRepository reasonRepository;

    @Transactional
    @Override
    public void closeReport(UUID reportId, UUID requestingAdmin) {
        var report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ReportDoesntExistException());
        if(report.getStatus() == Status.CLOSE)
            throw new ReportAlreadyClosedException();
        report.setStatus(Status.CLOSE);
        report.setResolvedBy(requestingAdmin);
        reportRepository.save(report);
    }

    @Override
    public ReportReasonTranslation createTranslation(ReportTranslationCreate reasonCreate) {
        return reportReasonI18nRepository.upsert(reasonCreate.reasonId(), reasonCreate.title(), reasonCreate.lang())
            .orElseThrow(() -> new ReportReasonDoesntExistException());
    }

    @Override
    public void deleteTranslation(Short reasonId, String lang) {
        var res = reportReasonI18nRepository.returningDelete(reasonId, lang);
        if(res == 0)
            throw new ReportTranslationDoesntExistException();
    }

    @Override
    public List<ReportReasonTranslation> getTranslations(Short reasonId) {
        return reportReasonI18nRepository.findByReasonId(reasonId);
    }

    @Override
    public Slice<ReportResponse> getReports(ReportFilter reportFilter, Pageable pageable) {
        return reportQueryRepository.getReports(reportFilter, pageable);
    }

    @Override
    public List<ReportReason> getReasons() {
        return reasonRepository.findBy();
    }
}
