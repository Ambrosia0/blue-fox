package com.ambrosia.library_policy.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.ActorArgumentResolver;
import com.ambrosia.library_policy.policy.GenericActorArgumentResolver;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
import com.ambrosia.library_policy.policy.PolicyContext;
import com.ambrosia.library_policy.policy.registry.PolicyHandler;

@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(ActorArgumentResolver.class)
@AutoConfiguration 
public class PolicyContextAutoConfiguration {

    @Bean 
    @ConditionalOnMissingBean(ActorArgumentResolver.class)
    ActorArgumentResolver actorArgumentResolver() {
        return new GenericActorArgumentResolver();
    }

    @ConditionalOnMissingBean(PolicyContext.class) 
    @Bean 
    GenericPolicyContext policyContext(List<PolicyHandler<Actor, ?, ?>> handlers){
        return new GenericPolicyContext(handlers);
    }

    @Bean 
    WebMvcConfigurer policyContextWebMvcConfigurer(ActorArgumentResolver actorArgumentResolver){
        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(actorArgumentResolver);
            }
        };
    }
}
