package com.ambrosia.library_policy.policy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ambrosia.library_policy.policy.registry.PolicyData;
import com.ambrosia.library_policy.policy.registry.PolicyHandler;

public class PolicyContext<T extends PolicyActor> {
    private final Map<
            Class<? extends Policy<?, ?>>, 
            PolicyHandler<T, ?, ?>> handlers;

    public PolicyContext(List<PolicyHandler<T, ?, ?>> handlers){
        this.handlers = handlers.stream()
            .collect(Collectors.toUnmodifiableMap(PolicyHandler::policyType, v -> v));
    }

    @SuppressWarnings("unchecked")
    public PolicyData evaluate(T actor, Class<? extends Policy<?, ?>> policy, Object arg){
        var res = handlers.get(policy);
        if(res == null)
            throw new IllegalArgumentException("No hanlder for policy: "+policy);
        return ((PolicyHandler<T, ?, Object>)res).evaluate(actor, arg);
    }
}
