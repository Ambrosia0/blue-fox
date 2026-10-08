package com.ambrosia.library_policy.policy;

import java.util.List;

import com.ambrosia.library_policy.policy.registry.PolicyHandler;

public class GenericPolicyContext extends PolicyContext<Actor> {
    public GenericPolicyContext(List<PolicyHandler<Actor, ?, ?>> handlers){
        super(handlers);
    }
}
