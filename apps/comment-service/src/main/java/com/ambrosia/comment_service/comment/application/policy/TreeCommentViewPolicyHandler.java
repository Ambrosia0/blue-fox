package com.ambrosia.comment_service.comment.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.TreeCommentViewPolicy;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class TreeCommentViewPolicyHandler implements GenericPolicyHandler<CommentPolicyData, Long>{
    private final TreeCommentViewPolicy treeCommentViewPolicy;

    private final PolicyDataRepository policyDataRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, Long commentId) {
        var data = policyDataRepository.loadByComment(actor.id(), commentId)
            .orElseThrow(() -> new CommentDoesntExistException());
        treeCommentViewPolicy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<CommentPolicyData>> policyType() {
        return TreeCommentViewPolicy.class;
    }
}