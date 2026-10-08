package com.ambrosia.comment_service.comment.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.CommentDeletePolicy;
import com.ambrosia.comment_service.comment.domain.policy.entity.DeletePolicyData;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CommentDeletePolicyHandler implements GenericPolicyHandler<DeletePolicyData, Long>{
    private final CommentDeletePolicy commentDeletePolicy;

    private final PolicyDataRepository policyDataRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, Long commentId) {
        var data = policyDataRepository.loadDeleteData(actor.id(), commentId)
            .orElseThrow(() -> new CommentDoesntExistException());
        commentDeletePolicy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<DeletePolicyData>> policyType() {
        return CommentDeletePolicy.class;
    }
}
