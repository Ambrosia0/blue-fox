package com.ambrosia.profile_service.user.domain.policy;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.profile_service.config.AppConfiguration;
import com.ambrosia.profile_service.exception.api.user.UsernameAlreadyClaimedException;
import com.ambrosia.profile_service.exception.api.user.UsernameChangeIntervalException;
import com.ambrosia.profile_service.user.domain.policy.entity.UsernameChangePolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class UsernameChangePolicy implements GenericPolicy<UsernameChangePolicyData>{
    private final AppConfiguration appConfiguration;
    
    @Override
    public void evaluate(Actor actor, UsernameChangePolicyData data) {
        if(data.isUsernameClaimed())
            throw new UsernameAlreadyClaimedException();
        if(data.lastChangeInstant() != null && 
                data.lastChangeInstant().plus(appConfiguration.getUsernameChangeInterval())
                    .isAfter(Instant.now())
        )
            throw new UsernameChangeIntervalException(
                appConfiguration.getUsernameChangeInterval(), 
                data.lastChangeInstant()
            );
    }
}
