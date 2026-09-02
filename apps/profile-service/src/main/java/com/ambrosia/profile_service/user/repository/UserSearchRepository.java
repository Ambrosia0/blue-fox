package com.ambrosia.profile_service.user.repository;

import java.util.List;

import com.ambrosia.profile_service.user.model.dto.response.UserSearch;

public interface UserSearchRepository {
    List<UserSearch> search(String searchString, int pageSize);
}
