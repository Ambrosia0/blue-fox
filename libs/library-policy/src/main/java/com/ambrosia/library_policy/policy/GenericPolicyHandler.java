package com.ambrosia.library_policy.policy;

import com.ambrosia.library_policy.policy.registry.PolicyData;
import com.ambrosia.library_policy.policy.registry.PolicyHandler;

public interface GenericPolicyHandler<D extends PolicyData, T> 
    extends PolicyHandler<
        Actor, 
        GenericPolicy<D>, 
        T
    > {}
