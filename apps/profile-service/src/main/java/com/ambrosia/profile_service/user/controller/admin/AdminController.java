package com.ambrosia.profile_service.user.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.profile_service.user.model.dto.admin.UserFilter;
import com.ambrosia.profile_service.user.model.dto.admin.UserResponse;
import com.ambrosia.profile_service.user.service.admin.AdminProfileService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/profile")
public class AdminController {
    private final AdminProfileService adminService;

    @PostMapping("/{id}/ban")
    public void banUser(@PathVariable UUID userId) {
        adminService.banUser(userId);
    }

    @PostMapping("/{id}/unban")
    public void unbanUser(@PathVariable UUID userId) {
        adminService.unbanUser(userId);
    }
    
    @GetMapping("/")
    public Slice<UserResponse> getUsers(
        @ModelAttribute UserFilter userFilter,
        @PageableDefault(page = 0, size = 20, direction = Direction.DESC) Pageable pageable) {
        return adminService.getUsers(userFilter, pageable);
    }

    // @GetMapping("/unbanRequests")
    // public Page<UnbanRequestResponse> getUnbanRequests(
    //     @PageableDefault(page = 0, size = 20, direction = Direction.DESC) Pageable pageable) {
    //     return adminService.getUnbanRequests(pageable);
    // }
}
