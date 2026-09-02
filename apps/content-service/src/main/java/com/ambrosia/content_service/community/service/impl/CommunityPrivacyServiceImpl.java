package com.ambrosia.content_service.community.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.content_service.community.service.CommunityPrivacyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPrivacyServiceImpl implements CommunityPrivacyService {
    private final CommunityProjectionRepository communityProjectionRepository;

    @Override
    public Optional<Boolean> isPrivate(long communityId) {
        return communityProjectionRepository.findIsCommunityPrivate(communityId);
    }
}
