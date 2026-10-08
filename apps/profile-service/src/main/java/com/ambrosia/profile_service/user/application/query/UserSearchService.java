package com.ambrosia.profile_service.user.application.query;

import java.util.List;

import com.ambrosia.profile_service.user.api.dto.response.UserSearch;

/**
 * Service for searching users
 */
public interface UserSearchService {
    List<UserSearch> search(String searchString, int pageSize);
}
