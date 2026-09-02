package com.ambrosia.community_service.community.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambrosia.community_service.community.model.entity.Community;

public interface AdminCommunityService {
    Page<Community> getCommunities(Pageable pageable);
}
