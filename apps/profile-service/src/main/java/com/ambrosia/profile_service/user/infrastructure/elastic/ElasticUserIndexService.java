package com.ambrosia.profile_service.user.infrastructure.elastic;


import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.ambrosia.outbox.elastic.SearchIndexOutboxService;
import com.ambrosia.profile_service.user.api.dto.response.UserSearch;
import com.ambrosia.profile_service.user.application.query.UserSearchService;
import com.ambrosia.profile_service.user.domain.entity.User;
import com.ambrosia.profile_service.user.infrastructure.UserIndexService;
import com.ambrosia.profile_service.user.infrastructure.entity.ElasticUser;

import lombok.RequiredArgsConstructor;
/**
 * Elasticsearch-based implementaion of user {@link User} search operations
 */
@Profile({"!es-disabled"})
@Primary
@RequiredArgsConstructor
@Service
public class ElasticUserIndexService implements UserIndexService, UserSearchService{
    private final SearchIndexOutboxService searchIndexOutboxService;

    private final ElasticUserSearchRepository elasticUserSearchRepository;

    @Override
    public void index(User user) {
        searchIndexOutboxService.put(convert(user)
            .isNew(true)
            .build()
        );
    }

    @Override
    public void reIndex(User user) {
        searchIndexOutboxService.put(convert(user)
            .isNew(false)
            .build()
        );
    }

    @Override
    public void removeFromIndex(String id, Long version) {
        searchIndexOutboxService.put(ElasticUser.builder()
            .id(UUID.fromString(id))
            .version(version)
            .build()
        );
    }

    @Override
    public List<UserSearch> search(String searchString, int pageSize) {
        return elasticUserSearchRepository.search(searchString, pageSize);
    }

    private ElasticUser.ElasticUserBuilder convert(User user){
        return ElasticUser.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .followCount(user.getFollowCount())
            .avatarId(user.getAvatarId())
            .username(user.getUsername())
            .version(user.getVersion());
    }
}
