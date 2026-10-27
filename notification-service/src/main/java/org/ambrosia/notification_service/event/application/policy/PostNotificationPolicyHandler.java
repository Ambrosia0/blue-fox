package org.ambrosia.notification_service.event.application.policy;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.core.policy.PolicyHandler;
import org.ambrosia.notification_service.exception.PostDoesntExistException;

@ApplicationScoped
public class PostNotificationPolicyHandler implements PolicyHandler<PostNotificationPolicy, Long> {
    private final PolicyDataRepository policyDataRepository;
    
    @Inject
    PostNotificationPolicy postNotificationPolicy;
    
    public PostNotificationPolicyHandler(PolicyDataRepository policyDataRepository) {
        this.policyDataRepository = policyDataRepository;
    }
    
    @Override
    public Uni<Void> validate(Actor actor, Long arg) {
        return policyDataRepository.loadForNotification(actor.id(), arg)
            .onItem()
            .transform(policyData -> policyData
                .orElseThrow(() -> new PostDoesntExistException())
            )
            .invoke(policyData -> postNotificationPolicy.validate(actor, policyData))
            .replaceWithVoid();
    }
    
    @Override
    public Class<PostNotificationPolicy> getType() {
        return PostNotificationPolicy.class;
    }
}
