package com.ambrosia.profile_service.user.service.elastic;


import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.ambrosia.outbox.elastic.SearchIndexOutboxService;
import com.ambrosia.profile_service.core.UserIndexService;
import com.ambrosia.profile_service.user.model.dto.response.UserSearch;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.model.entity.elastic.ElasticUser;
import com.ambrosia.profile_service.user.repository.elastic.ElasticUserSearchRepository;
import com.ambrosia.profile_service.user.service.UserSearchService;

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
    public void removeFromIndex(String id) {
        searchIndexOutboxService.put(ElasticUser.builder()
            .id(UUID.fromString(id))
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
