package com.ambrosia.report_service.report.application;

import java.util.List;
import java.util.UUID;

import com.ambrosia.report_service.report.api.dto.ReportReasonResponse;
import com.ambrosia.report_service.report.api.dto.ReportRequest;

public interface UserReportService {
    void createReport(ReportRequest reportRequest, UUID requestingUser);
    List<ReportReasonResponse> getReportReasons(String lang);
}
