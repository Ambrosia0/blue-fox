package com.ambrosia.report_service.report.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.report_service.report.api.dto.ReportFilter;
import com.ambrosia.report_service.report.api.dto.ReportReasonTranslation;
import com.ambrosia.report_service.report.api.dto.ReportResponse;
import com.ambrosia.report_service.report.api.dto.ReportTranslationCreate;
import com.ambrosia.report_service.report.application.AdminReportService;
import com.ambrosia.report_service.report.application.query.ReportQueryService;
import com.ambrosia.report_service.report.domain.entity.ReportReason;
import com.ambrosia.report_service.report.utils.Iso6391;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@RestController
public class AdminReportController {
    private final AdminReportService adminReportService;

    private final ReportQueryService reportQueryService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/status")
    public void closeRequest(
        @PathVariable UUID id,
        @AuthenticationPrincipal Jwt jwt) {
        adminReportService.closeReport(id, UUID.fromString(jwt.getSubject()));
    }

    @PostMapping("/reason/{id}/translation")
    public ReportReasonTranslation createReasonTranslation(
        @RequestBody @Valid ReportTranslationCreate translationCreate) {
        return adminReportService.createTranslation(translationCreate);
    }

    @DeleteMapping("/reason/{id}/translation/{lang}")
    public void deleteTranslation(
        @PathVariable Short reasonId,
        @PathVariable @Valid @Iso6391 String lang){
        adminReportService.deleteTranslation(reasonId, lang);
    }
    
    @GetMapping("/reason/{id}/translation")
    public List<ReportReasonTranslation> getTranslations(
        @PathVariable Short id) {
        return adminReportService.getTranslations(id);
    }
    
    @GetMapping
    public Slice<ReportResponse> getReports(
        @ModelAttribute ReportFilter reportFilter,
        Pageable pageable) {
        return reportQueryService.getReports(reportFilter, pageable);
    }

    @GetMapping("/reasons")
    public List<ReportReason> getReasons() {
        return adminReportService.getReasons();
    }
}
