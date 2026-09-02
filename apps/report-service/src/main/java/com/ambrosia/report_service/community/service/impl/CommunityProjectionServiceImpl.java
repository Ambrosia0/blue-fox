package com.ambrosia.report_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.community_service.kafka_events.CommunityDelete;
import com.ambrosia.report_service.community.entity.CommunityProjection;
import com.ambrosia.report_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.report_service.community.service.CommunityProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityProjectionServiceImpl implements CommunityProjectionService{
    private final CommunityProjectionRepository communityProjectionRepository;

    @Override
    public void create(CommunityCreate communityCreate, UUID eventId) {
        communityProjectionRepository.insert(
            CommunityProjection.create(communityCreate.getId()),
            eventId
        );
    }

    @Override
    public void delete(CommunityDelete communityDelete, UUID eventId) {
        communityProjectionRepository.delete(
            communityDelete.getId(),
            eventId
        );
    }

    @Override
    public boolean exist(Long id) {
        return communityProjectionRepository.existsById(id);
    }
}
