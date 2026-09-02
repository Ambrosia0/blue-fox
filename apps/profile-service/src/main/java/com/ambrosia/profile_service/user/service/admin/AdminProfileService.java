package com.ambrosia.profile_service.user.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.profile_service.user.model.dto.admin.UserFilter;
import com.ambrosia.profile_service.user.model.dto.admin.UserResponse;
import com.ambrosia.profile_service.user.model.dto.response.UnbanRequestResponse;

public interface AdminProfileService {
    void banUser(UUID userId);
    void unbanUser(UUID userId);
    Page<UnbanRequestResponse> getUnbanRequests(Pageable pageable);
    Slice<UserResponse> getUsers(UserFilter userFilter, Pageable pageable); 
}
