package com.ambrosia.profile_service.user.application.query;

import java.util.List;

import com.ambrosia.profile_service.user.api.dto.response.UserSearch;

public interface UserSearchRepository {
    List<UserSearch> search(String searchString, int pageSize);
}
