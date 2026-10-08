package com.ambrosia.profile_service.user.application.policy;

import java.util.UUID;

import com.ambrosia.profile_service.user.domain.policy.entity.UsernameChangePolicyData;

public interface  PolicyDataRepository {
    UsernameChangePolicyData loadChangeData(UUID userId, String username);
}
