package com.ambrosia.report_service.report.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.report.api.dto.ReportFilter;
import com.ambrosia.report_service.report.application.query.ReportQueryService;
import com.ambrosia.report_service.report.domain.entity.Report;
import com.ambrosia.report_service.report.domain.entity.Report.Status;
import com.ambrosia.report_service.report.domain.entity.Report.TargetType;
import com.ambrosia.report_service.util.ReportCreator;

public class ReportQueryServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired ReportCreator reportCreator;

    @Autowired ReportQueryService reportQueryService;

    @Test
    void shouldReturnReports(){
        var created = List.of(
            reportCreator.create(Status.OPEN, TargetType.POST),
            reportCreator.create(Status.OPEN, TargetType.USER),
            reportCreator.create(Status.CLOSE, TargetType.COMMENT)
        )
            .stream()
            .map(Report::getId)
            .collect(Collectors.toSet());
        assertEquals(
            created.size(),
            reportQueryService.getReports(
               ReportFilter.builder().build(),
               PageRequest.of(0, 10)
            )
                .getContent()
                .stream()
                .filter(t -> created.contains(t.id()))
                .count()
        );
    }

}
