package com.ambrosia.report_service.report.domain.policy;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.PolicyActor;
import com.ambrosia.library_policy.policy.registry.PolicyData;
import com.ambrosia.report_service.exception.report.InvalidReportTargetKeyException;
import com.ambrosia.report_service.exception.report.ReportReasonDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTargetDoesntExistException;
import com.ambrosia.report_service.report.domain.policy.entity.ReportPolicyData;
import org.springframework.stereotype.Component;

@Component
public class ReportCreatePolicy implements GenericPolicy<ReportPolicyData> {
    @Override
    public void evaluate(Actor actor, ReportPolicyData policyData) {
        if(!policyData.isReasonExist())
            throw new ReportReasonDoesntExistException();
        
        if(!policyData.isResourceExist())
            throw new ReportTargetDoesntExistException();
    }
}
