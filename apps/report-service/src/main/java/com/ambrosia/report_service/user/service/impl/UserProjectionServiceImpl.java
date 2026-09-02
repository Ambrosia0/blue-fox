package com.ambrosia.report_service.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.report_service.user.entity.UserProjection;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import com.ambrosia.report_service.user.service.UserProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProjectionServiceImpl implements UserProjectionService{
    private final UserProjectionRepository userProjectionRepository;

    @Override
    public void create(UserCreated created, UUID eventId) {
        userProjectionRepository.insert(
            UserProjection.create(
                UUID.fromString(created.getId())
            ),
            eventId
        );
        
    }
    @Override
    public boolean exist(UUID id) {
        return userProjectionRepository.existsById(id);
    }
}
