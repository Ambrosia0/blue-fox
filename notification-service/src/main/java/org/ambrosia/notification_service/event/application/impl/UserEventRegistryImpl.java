package org.ambrosia.notification_service.event.application.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.core.AppConfiguration;
import org.ambrosia.notification_service.event.application.UserEventDispatcher;
import org.ambrosia.notification_service.event.application.UserEventRegistry;
import org.ambrosia.notification_service.event.domain.UserFollowRepository;
import org.ambrosia.notification_service.event.infrastructure.entity.UserFollow;
import org.ambrosia.notification_service.kafka_events.Notification;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserEventRegistryImpl implements UserEventRegistry, UserEventDispatcher {
    private final Map<UUID, UserData> userData = new ConcurrentHashMap<>();
    
    private final Map<Long, Set<UUID>> communitySearchIndex = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> userFollowSearchIndex = new ConcurrentHashMap<>();
    
    private final UserFollowRepository userFollowRepository;
    
    private final EventBus eventBus;
    
    public UserEventRegistryImpl(
            UserFollowRepository userFollowRepository,
            EventBus eventBus
    ) {
        this.userFollowRepository = userFollowRepository;
        this.eventBus = eventBus;
    }
    
    @Override
    public Multi<Message<String>> register(UUID userId) {
        var consumer = eventBus.<String>consumer("event.user."+userId.toString());
        return consumer.toMulti()
            .onSubscription().call(() -> add(userId))
            .onTermination().invoke(() -> remove(userId))
            .onTermination().call(consumer::unregister);
    }
    
    public void sendMessage(Notification notification){
        switch (notification.getPayloadCase()){
            case COMMUNITY_NOTIFICATION -> {
                var payload = notification.getCommunityNotification();
                communitySearchIndex.computeIfPresent(
                    payload.getCommunityId(),
                    (_, v) -> {
                        v.forEach(userId -> eventBus.publish(
                            "event.user."+userId.toString(),
                            payload.getContent()
                        ));
                        return v;
                    }
                );
            }
            case TARGETED_NOTIFICATION -> {
                var payload = notification.getTargetedNotification();
                eventBus.publish("event.user."+payload.getUserId(), payload.getContent());
            }
            case USER_NOTIFICATION -> {
                var payload = notification.getUserNotification();
                userFollowSearchIndex.computeIfPresent(
                    UUID.fromString(payload.getUserId()),
                    (k, v) -> {
                        v.forEach(userId -> eventBus.publish(
                            "event.user."+userId.toString(),
                            payload.getContent()
                        ));
                        return v;
                    }
                );
            }
            default -> throw new IllegalArgumentException("Invalid notification payload!");
        }
    }
    
    private Uni<Void> add(UUID userId){
        return userFollowRepository.getFollows(userId)
            .onSubscription()
                .invoke(() -> userData.compute(userId, (_, userData) ->
                        userData == null?
                            UserData.create():
                            userData
                    )
                )
            .onItem()
                .invoke(follows ->
                    userData.compute(userId, (_, userData) -> {
                        var data = userData;
                        if (data == null)
                            data = UserData.create();
                        for (UserFollow follow : follows) {
                            if (follow.communityId() != null) {
                                data.followedCommunities.add(follow.communityId());
                                communitySearchIndex.compute(
                                    follow.communityId(),
                                    (_, uuids) -> addUserToSet(uuids, userId)
                                );
                            }
                            if (follow.followedUserId() != null) {
                                data.followedUsers.add(follow.followedUserId());
                                userFollowSearchIndex.compute(
                                    follow.followedUserId(),
                                    (_, uuids) -> addUserToSet(uuids, userId)
                                );
                            }
                        }
                        return data;
                    })
                )
            .replaceWithVoid();
    }
    
    // removes user follows from local index
    private void remove(UUID userId){
        userData.compute(userId, (_, userFollows) -> {
            if(userFollows == null)
                return null;
            userFollows.followedUsers().forEach(followedUserId -> userFollowSearchIndex
                .compute(followedUserId, (_, idSet) -> {
                    if(idSet == null)
                        return null;
                    
                    idSet.remove(userId);
                    
                    if(idSet.isEmpty())
                        return null;
                    
                    return idSet;
                })
            );
            userFollows.followedCommunities().forEach(followedCommunityId -> communitySearchIndex
                .compute(followedCommunityId, (_, idSet) -> {
                    if(idSet == null)
                        return null;
                    idSet.remove(userId);
                    
                    if(idSet.isEmpty())
                        return null;
                    
                    return idSet;
                })
            );
            return null;
        });
    }
    
    private Set<UUID> addUserToSet(Set<UUID> set, UUID userId){
        var ids = set;
        if(ids == null){
            ids = new HashSet<UUID>();
        }
        ids.add(userId);
        return ids;
    }

    private record UserData(
        Set<UUID> followedUsers,
        Set<Long> followedCommunities
    ){
        public static UserData create(){
            return new UserData(
                new HashSet<UUID>(),
                new HashSet<Long>()
            );
        }
    }
}