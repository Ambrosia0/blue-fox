package com.ambrosia.library_policy.policy.registry;

import com.ambrosia.library_policy.policy.Policy;
import com.ambrosia.library_policy.policy.PolicyActor;

/**
 * PolicyHandler
 * @param <A> actor type
 * @param <P> policy type
 * @param <T> argument/resource type
 */
public interface PolicyHandler<
        A extends PolicyActor, 
        P extends Policy<A, ? extends PolicyData>, 
        T
    > {
    PolicyData evaluate(A actor, T arg);
    Class<? extends P> policyType();
}
