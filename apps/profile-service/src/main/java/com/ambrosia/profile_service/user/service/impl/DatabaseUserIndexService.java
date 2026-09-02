package com.ambrosia.profile_service.user.service.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.core.UserIndexService;
import com.ambrosia.profile_service.user.model.dto.response.UserSearch;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.repository.UserSearchRepository;
import com.ambrosia.profile_service.user.service.UserSearchService;

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
    public void removeFromIndex(String id) {}

    @Override
    public List<UserSearch> search(String searchString, int pageSize) {
        return userSearchRepository.search(searchString, pageSize);
    }
}
