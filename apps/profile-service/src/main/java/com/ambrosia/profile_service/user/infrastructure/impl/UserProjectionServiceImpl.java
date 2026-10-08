package com.ambrosia.profile_service.user.infrastructure.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.outbox.kafka.KafkaOutboxService;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.infrastructure.kafka.mapper.UserProjectionMapper;
import com.ambrosia.profile_service.infrastructure.kafka.utils.UserEventFactory;
import com.ambrosia.profile_service.user.api.dto.UserProjection;
import com.ambrosia.profile_service.user.infrastructure.UserIndexService;
import com.ambrosia.profile_service.user.infrastructure.UserProjectionService;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserProjectionServiceImpl implements UserProjectionService{
    private final JdbcUserRepository userRepository;

    private final UserIndexService userIndexService;

    private final ApplicationEventPublisher eventPublisher;

    private final KafkaOutboxService kafkaOutboxService;

    private final UserProjectionMapper userProjectionMapper;

    @Transactional
    @Override
    public void create(UserProjection userProjection) {
        try {
            var user = userRepository.save(
                userProjectionMapper.toEntity(userProjection)
            );
            userIndexService.index(user);

            var event = UserEventFactory.createdEvent(user);
            kafkaOutboxService.put(event);
            eventPublisher.publishEvent(event);
            log.info("USER CREATE: {}", user.getId());
        } catch (RuntimeException e) {
            log.error("Can't create user!", e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void update(UserProjection userProjection) {
        try {
            var user = userRepository.findById(userProjection.id())
                .orElseThrow(() -> new UserDoesntExistException());

            user.setEnabled(userProjection.enabled());
            user.setEmail(userProjection.email());
            user.setAvatarId(userProjection.avatarId());
            user.setUsername(userProjection.username());
            user.setFirstName(userProjection.firstName());
            user.setLastName(userProjection.lastName());

            if(userProjection.role() != null) 
                user.setRole(userProjection.role());
                
            user = userRepository.save(user);

            var event = UserEventFactory.updatedEvent(user);
            kafkaOutboxService.put(event);
            eventPublisher.publishEvent(event);

            userIndexService.reIndex(user);
            log.debug("USER UPDATE: {}", user.getId());
        } catch (RuntimeException e) {
            log.error("Can't update user!", e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        try {
            var deletedOpt = userRepository.returningDeleteById(id);
            if(deletedOpt.isEmpty())
                return;

            var deleted = deletedOpt.get();

            var event = UserEventFactory.deletedEvent(deleted.id());
            kafkaOutboxService.put(event);
            eventPublisher.publishEvent(event);

            userIndexService.removeFromIndex(
                deleted.id().toString(), 
                deleted.version()
            );
            log.debug("USER DELETE: {}", id);   
        } catch (RuntimeException e) {
            log.error("Can't delete user!", e);
            throw e;
        }
    }
}
