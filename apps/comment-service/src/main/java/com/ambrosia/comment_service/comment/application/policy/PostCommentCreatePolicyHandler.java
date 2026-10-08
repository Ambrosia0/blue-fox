package com.ambrosia.comment_service.comment.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.PostCommentCreatePolicy;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.exceptions.api.PostDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class PostCommentCreatePolicyHandler implements GenericPolicyHandler<CommentPolicyData, Long> {
    private final PostCommentCreatePolicy rootCommentCreatePolicy;

    private final PolicyDataRepository policyDataRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, Long postId) {
        var data = policyDataRepository.loadByPost(actor.id(), postId)
            .orElseThrow(() -> new PostDoesntExistException());
        rootCommentCreatePolicy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<CommentPolicyData>> policyType() {
        return PostCommentCreatePolicy.class;
    }
}
