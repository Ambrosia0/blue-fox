package com.ambrosia.report_service.report.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.report_service.report.model.dto.ReportReasonResponse;
import com.ambrosia.report_service.report.model.dto.ReportRequest;

public interface UserReportService {
    void createReport(ReportRequest reportRequest, UUID requestingUser);
    List<ReportReasonResponse> getReportReasons(String lang);
}
