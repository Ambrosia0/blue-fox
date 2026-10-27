package com.ambrosia.report_service.report.domain.policy.entity;


import com.ambrosia.library_policy.policy.registry.PolicyData;

public record ReportPolicyData(
    boolean isReasonExist,
    boolean isResourceExist
) implements PolicyData {}
