package com.ambrosia.content_service.user.service.impl;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.user.repository.UserRepository;
import com.ambrosia.content_service.user.service.UserProjectionEventProcessor;
import com.ambrosia.content_service.user.service.UserService;
import com.ambrosia.content_service.user.service.mapper.UserProjectionMapper;
import com.ambrosia.profile_service.kafka_events.UserEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class UserProjectionEventProcessorImpl implements UserProjectionEventProcessor, UserService{
    private final UserRepository userRepository;
    
    private final UserProjectionMapper userProjectionMapper;

    @Override
    public void process(UserEvent userEvent) {
        var eventId = UUID.fromString(userEvent.getEventId());
        var userId = UUID.fromString(userEvent.getUserId());
        switch (userEvent.getPayloadCase()) {
            case CREATED -> {
                userRepository.insert(
                    userProjectionMapper.toEntity(userId, userEvent.getCreated()), 
                    eventId
                );
            }
            case DELETED -> {
                userRepository.delete(
                    userId, 
                    eventId
                );
            }
            case UPDATED -> {
                userRepository.update(
                    userProjectionMapper.toEntity(userId, userEvent.getUpdated()), 
                    eventId
                );
            }
            default ->{}
        }
    }

    @Override
    public boolean isUsersExist(Set<UUID> userIds) {
        return userRepository.count(userIds) == userIds.size();
    }

    @Override
    public boolean isUserExist(UUID userId) {
        return userRepository.existsById(userId);
    }
}
