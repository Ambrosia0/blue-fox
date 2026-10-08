package com.ambrosia.community_service.community.application.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.application.CommunityModeratorService;
import com.ambrosia.community_service.community.domain.entity.CommunityBan;
import com.ambrosia.community_service.community.domain.repository.CommunityBanRepository;
import com.ambrosia.community_service.core.application.policy.ModeratorPolicyArg;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityBanPolicy;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityUnbanPolicy;
import com.ambrosia.community_service.infrastructure.kafka.utils.CommunityBanEventFactory;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityModeratorServiceImpl implements CommunityModeratorService{
    private final CommunityBanRepository communityBanRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final GenericPolicyContext genericPolicyContext;

    @Override
    public void banUser(long communityId, Actor actor, UUID userToBan, Instant beforeDate) {
        genericPolicyContext.evaluate(
            actor, 
            CommunityBanPolicy.class, 
            ModeratorPolicyArg.create(communityId, userToBan)
        );
        communityBanRepository.save(CommunityBan.create(userToBan, communityId, beforeDate));
        eventPublisher.publishEvent(
            CommunityBanEventFactory.createBan(communityId, userToBan)
        );

    }

    @Override
    public void unbanUser(long communityId, Actor actor, UUID userToUnban) {
        genericPolicyContext.evaluate(
            actor, 
            CommunityUnbanPolicy.class, 
            ModeratorPolicyArg.create(communityId, userToUnban)
        );
        communityBanRepository.unban(userToUnban, communityId);
        eventPublisher.publishEvent(
            CommunityBanEventFactory.createUnban(communityId, userToUnban)
        );
    }
}
