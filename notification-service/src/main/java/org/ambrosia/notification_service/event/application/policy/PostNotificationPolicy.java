package org.ambrosia.notification_service.event.application.policy;

import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.core.policy.Policy;
import org.ambrosia.notification_service.exception.UserBannedException;
import org.ambrosia.notification_service.exception.UserDoesntFollowedException;
import org.ambrosia.notification_service.event.domain.policy.PostNotificationPolicyData;

@ApplicationScoped
public class PostNotificationPolicy implements Policy<PostNotificationPolicyData> {
    @Override
    public void validate(Actor actor, PostNotificationPolicyData policyData) {
        if(actor.role() == Actor.Role.ADMIN || policyData.isModerator())
            return;
        if(policyData.isPrivate()){
            if(policyData.isBanned())
                throw new UserBannedException();
            if(!policyData.isFollowed())
                throw new UserDoesntFollowedException();
        }
    }
}
