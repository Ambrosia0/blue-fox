package com.ambrosia.profile_service.user.application.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.user.api.dto.UserFilter;
import com.ambrosia.profile_service.user.api.dto.UserResponse;
import com.ambrosia.profile_service.user.api.dto.response.UnbanRequestResponse;
import com.ambrosia.profile_service.user.application.AdminProfileService;
import com.ambrosia.profile_service.user.application.IdpAdminService;
import com.ambrosia.profile_service.user.application.query.UserQueryRepository;
import com.ambrosia.profile_service.user.domain.repository.UnbanRequestRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminProfileServiceImpl implements AdminProfileService {
    private final UnbanRequestRepository unbanRequestRepository;

    private final UserQueryRepository userQueryRepository;

    private final IdpAdminService idpAdminService;

    @Override
    public void banUser(UUID userId) {
        idpAdminService.banUser(userId);
    }
    
    @Override
    public Page<UnbanRequestResponse> getUnbanRequests(Pageable pageable) {
        return unbanRequestRepository.findByIsViewedIsFalse(pageable);
    }

    @Override
    public Slice<UserResponse> getUsers(UserFilter userFilter, Pageable pageable) {
        return userQueryRepository.getUsers(userFilter, pageable);
    }

    @Override
    public void unbanUser(UUID userId) {
        idpAdminService.unbanUser(userId);
    }
}
