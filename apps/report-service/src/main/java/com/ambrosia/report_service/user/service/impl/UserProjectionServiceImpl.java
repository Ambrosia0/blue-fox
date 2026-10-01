package com.ambrosia.report_service.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import com.ambrosia.report_service.user.service.UserProjectionService;
import com.ambrosia.report_service.user.service.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProjectionServiceImpl implements UserProjectionService{
    private final UserProjectionRepository userProjectionRepository;

    private final UserMapper userMapper;

    @Override
    public void process(UserEvent userEvent) {
        var eventId = UUID.fromString(userEvent.getEventId());
        var userId = UUID.fromString(userEvent.getUserId());
        switch (userEvent.getPayloadCase()) {
            case CREATED -> {
                userProjectionRepository.insert(
                    userMapper.toEntity(userId, userEvent.getCreated()),
                    eventId
                );
            }
            case DELETED -> {
                userProjectionRepository.delete(
                    userId, 
                    eventId
                );
            }
            case UPDATED -> {
                userProjectionRepository.update(
                    userMapper.toEntity(userId, userEvent.getUpdated()),
                    eventId
                );
            }
            default -> {
                throw new RuntimeException("Unknown message payload!");
            }
        }
        
    }
    @Override
    public boolean exist(UUID id) {
        return userProjectionRepository.existsById(id);
    }
}
