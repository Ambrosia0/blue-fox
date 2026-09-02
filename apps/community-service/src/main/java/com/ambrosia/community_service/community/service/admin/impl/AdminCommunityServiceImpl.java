package com.ambrosia.community_service.community.service.admin.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.admin.AdminCommunityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminCommunityServiceImpl implements AdminCommunityService{
    private final CommunityRepository communityRepository;

    @Override
    public Page<Community> getCommunities(Pageable pageable) {
        return communityRepository.findAll(pageable);
    }
}
