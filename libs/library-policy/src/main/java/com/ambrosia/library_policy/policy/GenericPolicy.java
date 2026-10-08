package com.ambrosia.library_policy.policy;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public interface GenericPolicy<D extends PolicyData> 
    extends Policy<Actor, D>{}
