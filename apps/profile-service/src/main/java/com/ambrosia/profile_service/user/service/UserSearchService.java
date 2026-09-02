package com.ambrosia.profile_service.user.service;

import java.util.List;

import com.ambrosia.profile_service.user.model.dto.response.UserSearch;

/**
 * Service for searching users
 */
public interface UserSearchService {
    List<UserSearch> search(String searchString, int pageSize);
}
