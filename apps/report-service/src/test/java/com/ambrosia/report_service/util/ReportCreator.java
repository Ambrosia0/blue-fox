package com.ambrosia.report_service.util;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.report_service.report.domain.entity.Report;
import com.ambrosia.report_service.report.domain.entity.ReportReason;
import com.ambrosia.report_service.report.domain.entity.ReportReasonI18n;
import com.ambrosia.report_service.report.domain.repository.ReportReasonI18nRepository;
import com.ambrosia.report_service.report.domain.repository.ReportReasonRepository;
import com.ambrosia.report_service.report.domain.repository.ReportRepository;
import com.ambrosia.report_service.user.entity.UserProjection;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@TestComponent
public class ReportCreator {
    @Autowired
    UserProjectionRepository userProjectionRepository;

    @Autowired
    ReportReasonRepository reportReasonRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ReportReasonI18nRepository reasonI18nRepository;

    public Report create(Report.Status status, Report.TargetType targetType){
        var user = createUser();
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

    public ReportReasonI18n createTranslation(short reasonId, String lang){
        return reasonI18nRepository.save(ReportReasonI18n.create(
            reasonId,
            lang, 
            "Test title"
        ));
    }
    
    public UserProjection createUser(){
        var event = UserEvent.newBuilder()
            .setUserId(UUID.randomUUID().toString())
            .setEventId(UUID.randomUUID().toString())
            .setVersion(0)
            .setCreated(UserCreated.newBuilder()
                .setUsername("test"+rand())
                .setFirstName("test"+rand())
                .setLastName("test"+rand())
            )
            .build();
        userProjectionRepository.process(List.of(event), List.of());
        return userProjectionRepository.findById(UUID.fromString(event.getUserId()))
            .orElseThrow(RuntimeException::new);
    }
    
    private Long rand(){
        return ThreadLocalRandom.current().nextLong();
    }
}
