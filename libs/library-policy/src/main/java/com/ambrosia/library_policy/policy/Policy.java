package com.ambrosia.library_policy.policy;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public interface Policy<A extends PolicyActor, T extends PolicyData> {
    void evaluate(A actor, T policyData);
}
