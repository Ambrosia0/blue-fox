package com.ambrosia.profile_service.user.service.impl;

import java.util.Collection;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.outbox.kafka.KafkaOutboxService;
import com.ambrosia.profile_service.core.UserIndexService;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.kafka.utils.UserEventFactory;
import com.ambrosia.profile_service.user.model.dto.UserProjection;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.service.UserProjectionService;
import com.ambrosia.profile_service.user.service.mapper.UserProjectionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserProjectionServiceImpl implements UserProjectionService{
    private final UserRepository userRepository;

    private final UserIndexService userIndexService;

    private final ApplicationEventPublisher eventPublisher;

    private final KafkaOutboxService kafkaOutboxService;

    private final UserProjectionMapper userProjectionMapper;

    @Transactional
    @Override
    public void create(UserProjection userProjection) {
        try {
            var user = userRepository.save(userProjectionMapper.toEntity(userProjection));
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
            var original = userRepository.findById(userProjection.id())
                .orElseThrow(() -> new UserDoesntExistException());
                
            var user = userRepository.save(
                userProjectionMapper.apply(original, userProjection)
            );

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

    @Override
    public boolean isExists(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isExists(Collection<UUID> ids) {
        return userRepository.existsByIds(ids);
    }
}
