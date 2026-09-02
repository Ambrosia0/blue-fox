package com.ambrosia.profile_service.user.service.admin.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.core.idp.IdpAdminService;
import com.ambrosia.profile_service.user.model.dto.admin.UserFilter;
import com.ambrosia.profile_service.user.model.dto.admin.UserResponse;
import com.ambrosia.profile_service.user.model.dto.response.UnbanRequestResponse;
import com.ambrosia.profile_service.user.repository.UnbanRequestRepository;
import com.ambrosia.profile_service.user.repository.UserQueryRepository;
import com.ambrosia.profile_service.user.service.admin.AdminProfileService;

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
