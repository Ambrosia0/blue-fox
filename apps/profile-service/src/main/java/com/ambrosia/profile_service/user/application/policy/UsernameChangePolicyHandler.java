package com.ambrosia.profile_service.user.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;
import com.ambrosia.profile_service.user.domain.policy.UsernameChangePolicy;
import com.ambrosia.profile_service.user.domain.policy.entity.UsernameChangePolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class UsernameChangePolicyHandler implements GenericPolicyHandler<UsernameChangePolicyData, String> {
    private final UsernameChangePolicy policy;

    private final PolicyDataRepository policyDataRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, String arg) {
        var data = policyDataRepository.loadChangeData(actor.id(), arg);
        policy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<UsernameChangePolicyData>> policyType() {
        return UsernameChangePolicy.class;
    }
}
