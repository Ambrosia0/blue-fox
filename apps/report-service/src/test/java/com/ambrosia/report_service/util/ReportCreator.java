package com.ambrosia.report_service.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.report_service.report.domain.entity.Report;
import com.ambrosia.report_service.report.domain.entity.ReportReason;
import com.ambrosia.report_service.report.domain.entity.ReportReasonI18n;
import com.ambrosia.report_service.report.domain.repository.ReportReasonI18nRepository;
import com.ambrosia.report_service.report.domain.repository.ReportReasonRepository;
import com.ambrosia.report_service.report.domain.repository.ReportRepository;
import com.ambrosia.report_service.user.entity.UserProjection;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;

import io.github.robsonkades.uuidv7.UUIDv7;

@TestComponent 
public class ReportCreator {
    @Autowired UserProjectionRepository userProjectionRepository;

    @Autowired ReportReasonRepository reportReasonRepository;

    @Autowired ReportRepository reportRepository;

    @Autowired ReportReasonI18nRepository reasonI18nRepository;

    public Report create(Report.Status status, Report.TargetType targetType){
        var user = userProjectionRepository.insert(new UserProjection(
                UUID.randomUUID(), 
                "testusername",
                null,
                true
            ), 
            UUID.randomUUID()
        );
        var reason = reportReasonRepository.save(ReportReason.builder()
            .code("TestCode"+ThreadLocalRandom.current().nextLong())
            .build()
        );

        return reportRepository.save(Report.builder()
            .reportReasonId(reason.getId())
            .userId(user.getId())
            .reportContent("Test Content")
            .reportedContentKey("random string")
            .status(status)
            .targetType(targetType)
            .build()
        );
    }

    public ReportReason createReason(){
        return reportReasonRepository.save(ReportReason.builder()
            .code("TestCode"+ThreadLocalRandom.current().nextLong())
            .build()
        );
    }

    public UserProjection createUser(){
        return userProjectionRepository.insert(UserFactory.create(), UUIDv7.randomUUID());
    }

    public ReportReasonI18n createTranslation(short reasonId, String lang){
        return reasonI18nRepository.save(ReportReasonI18n.create(
            reasonId,
            lang, 
            "Test title"
        ));
    }
}
