package com.ambrosia.profile_service.user.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.profile_service.user.api.dto.UserFilter;
import com.ambrosia.profile_service.user.api.dto.UserResponse;
import com.ambrosia.profile_service.user.api.dto.response.UnbanRequestResponse;

public interface AdminProfileService {
    void banUser(UUID userId);
    void unbanUser(UUID userId);
    Page<UnbanRequestResponse> getUnbanRequests(Pageable pageable);
    Slice<UserResponse> getUsers(UserFilter userFilter, Pageable pageable); 
}
