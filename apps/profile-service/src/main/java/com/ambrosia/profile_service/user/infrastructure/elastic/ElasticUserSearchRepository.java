package com.ambrosia.profile_service.user.infrastructure.elastic;

import java.util.List;

import com.ambrosia.profile_service.user.api.dto.response.UserSearch;

public interface ElasticUserSearchRepository {
    List<UserSearch> search(String searchString, int pageSize);
}
