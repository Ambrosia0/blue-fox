package com.ambrosia.report_service.report.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.report_service.comment.service.CommentProjectionService;
import com.ambrosia.report_service.community.service.CommunityProjectionService;
import com.ambrosia.report_service.exception.report.InvalidReportTargetKeyException;
import com.ambrosia.report_service.exception.report.ReportReasonDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTargetDoesntExistException;
import com.ambrosia.report_service.post.service.PostProjectionService;
import com.ambrosia.report_service.report.model.dto.ReportReasonResponse;
import com.ambrosia.report_service.report.model.dto.ReportRequest;
import com.ambrosia.report_service.report.model.entity.Report;
import com.ambrosia.report_service.report.repository.ReportReasonRepository;
import com.ambrosia.report_service.report.repository.ReportRepository;
import com.ambrosia.report_service.report.service.UserReportService;
import com.ambrosia.report_service.user.service.UserProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserReportServiceImpl implements UserReportService{
    private final ReportRepository reportRepository;

    private final ReportReasonRepository reportReasonRepository;
    
    private final CommentProjectionService commentProjectionService;
    private final PostProjectionService postProjectionService;
    private final CommunityProjectionService communityProjectionService;
    private final UserProjectionService userProjectionService;

    @Override
    public void createReport(ReportRequest reportRequest, UUID requestingUser) {
        boolean isKeyValid = reportRequest.targetType().isValid(reportRequest.reportContentKey());
        if(!isKeyValid || requestingUser.toString().equals(reportRequest.reportContentKey()))
            throw new InvalidReportTargetKeyException();
        if(!reportReasonRepository.existsById(reportRequest.reportReasonId()))
            throw new ReportReasonDoesntExistException();
        var exist = switch (reportRequest.targetType()) {
            case COMMENT -> commentProjectionService.exist(Long.parseLong(reportRequest.reportContentKey()));
            case POST -> postProjectionService.exist(Long.parseLong(reportRequest.reportContentKey()));
            case USER -> userProjectionService.exist(UUID.fromString(reportRequest.reportContentKey()));
            case COMMUNITY -> communityProjectionService.exist(Long.parseLong(reportRequest.reportContentKey()));
        };
        if(!exist)
            throw new ReportTargetDoesntExistException();
        reportRepository.save(Report.builder()
            .reportContent(reportRequest.reportContent())
            .reportReasonId(reportRequest.reportReasonId())
            .targetType(reportRequest.targetType())
            .reportedContentKey(reportRequest.reportContentKey())
            .userId(requestingUser)
            .build()
        );
    }

    @Override
    public List<ReportReasonResponse> getReportReasons(String lang) {
        return reportReasonRepository.findByLang(lang);
    }
}
