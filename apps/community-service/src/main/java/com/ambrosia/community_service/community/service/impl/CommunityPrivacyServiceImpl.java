package com.ambrosia.community_service.community.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.CommunityPrivacyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPrivacyServiceImpl implements CommunityPrivacyService {
    private final CommunityRepository communityRepository;

    @Override
    public Optional<Boolean> isPrivate(long communityId) {
        return communityRepository.findIsCommunityPrivate(communityId);
    }
}
