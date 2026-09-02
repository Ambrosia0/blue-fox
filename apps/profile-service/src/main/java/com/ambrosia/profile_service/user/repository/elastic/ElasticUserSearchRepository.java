package com.ambrosia.profile_service.user.repository.elastic;

import java.util.List;

import com.ambrosia.profile_service.user.model.dto.response.UserSearch;

public interface ElasticUserSearchRepository {
    List<UserSearch> search(String searchString, int pageSize);
}
