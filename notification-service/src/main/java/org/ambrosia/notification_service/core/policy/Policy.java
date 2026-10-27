package org.ambrosia.notification_service.core.policy;

import io.smallrye.mutiny.Uni;

public interface Policy <T extends PolicyData>{
    void validate(Actor actor, T policyData);
}
