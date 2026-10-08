package com.ambrosia.profile_service.user.infrastructure.persistence;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.user.api.dto.response.UserSearch;
import com.ambrosia.profile_service.user.application.query.UserSearchRepository;
import com.ambrosia.profile_service.user.application.query.UserSearchService;
import com.ambrosia.profile_service.user.domain.entity.User;
import com.ambrosia.profile_service.user.infrastructure.UserIndexService;

import lombok.RequiredArgsConstructor;

/**
 * Database-based implementaion of user {@link User} search operations
 */
@Profile("es-disabled")
@RequiredArgsConstructor
@Service
public class DatabaseUserIndexService implements UserIndexService, UserSearchService{
    private final UserSearchRepository userSearchRepository;
    
    @Override
    public void index(User user) {}

    @Override
    public void reIndex(User user) {}

    @Override
    public void removeFromIndex(String id, Long version) {}

    @Override
    public List<UserSearch> search(String searchString, int pageSize) {
        return userSearchRepository.search(searchString, pageSize);
    }
}
