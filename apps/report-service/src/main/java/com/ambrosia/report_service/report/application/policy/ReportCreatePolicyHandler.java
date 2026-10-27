package com.ambrosia.report_service.report.application.policy;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;
import com.ambrosia.report_service.comment.service.CommentProjectionService;
import com.ambrosia.report_service.community.service.CommunityProjectionService;
import com.ambrosia.report_service.exception.report.InvalidReportTargetKeyException;
import com.ambrosia.report_service.post.service.PostProjectionService;
import com.ambrosia.report_service.report.api.dto.ReportRequest;
import com.ambrosia.report_service.report.domain.policy.ReportCreatePolicy;
import com.ambrosia.report_service.report.domain.policy.entity.ReportPolicyData;
import com.ambrosia.report_service.report.domain.repository.ReportReasonRepository;
import com.ambrosia.report_service.user.service.UserProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ReportCreatePolicyHandler implements GenericPolicyHandler<ReportPolicyData, ReportRequest> {
    private final ReportCreatePolicy reportCreatePolicy;
    
    private final CommentProjectionService commentProjectionService;
    
    private final PostProjectionService postProjectionService;
    
    private final CommunityProjectionService communityProjectionService;
    
    private final UserProjectionService userProjectionService;
    
    private final ReportReasonRepository reportReasonRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, ReportRequest arg) {
        if(!arg.targetType().isValid(arg.reportContentKey()) ||
                actor.id().toString().equals(arg.reportContentKey())){
            throw new InvalidReportTargetKeyException();
        }
        var data = new ReportPolicyData(
            reportReasonRepository.existsById(arg.reportReasonId()),
            switch (arg.targetType()) {
                case COMMENT -> commentProjectionService.exist(Long.parseLong(arg.reportContentKey()));
                case POST -> postProjectionService.exist(Long.parseLong(arg.reportContentKey()));
                case USER -> userProjectionService.exist(UUID.fromString(arg.reportContentKey()));
                case COMMUNITY -> communityProjectionService.exist(Long.parseLong(arg.reportContentKey()));
            }
        );
        
        reportCreatePolicy.evaluate(actor, data);
        return data;
    }
    
    @Override
    public Class<? extends GenericPolicy<ReportPolicyData>> policyType() {
        return reportCreatePolicy.getClass();
    }
}
