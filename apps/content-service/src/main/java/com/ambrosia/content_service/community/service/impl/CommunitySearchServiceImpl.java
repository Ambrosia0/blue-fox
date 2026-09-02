package com.ambrosia.content_service.community.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.content_service.community.service.CommunitySearchService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunitySearchServiceImpl implements CommunitySearchService{
    private final CommunityProjectionRepository communityProjectionRepository;

    @Override
    public Optional<CommunityProjection> findById(Long communityId) {
        return communityProjectionRepository.findById(communityId);
    }
}
