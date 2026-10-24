package org.ambrosia.notification_service.core.policy;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GenericPolicyContext implements PolicyContext{
    private final Map<Class<? extends Policy<?>>, PolicyHandler<?, ?>> policyHandlerMap;
    
    public GenericPolicyContext(Instance<PolicyHandler<?, ?>> policyHandlerList) {
        this.policyHandlerMap = policyHandlerList.stream()
            .collect(Collectors.toMap(PolicyHandler::getType, v -> v));
    }
    
    @Override
    public Uni<Void> validate(Actor actor, Class<? extends Policy<?>> policy, Object arg) {
        var handler = policyHandlerMap.get(policy);
        if(handler == null)
            throw new IllegalArgumentException("No suitable handler found for policy: " + policy.getClass());
        return ((PolicyHandler<?, Object>)handler).validate(actor, arg);
    }
}
