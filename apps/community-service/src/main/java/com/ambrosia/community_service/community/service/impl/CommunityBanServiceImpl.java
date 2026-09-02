package com.ambrosia.community_service.community.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.repository.CommunityBanRepository;
import com.ambrosia.community_service.community.service.CommunityBanService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityBanServiceImpl implements CommunityBanService{
    private final CommunityBanRepository communityBanRepository;

    @Override
    public boolean isAnyBanned(List<UUID> ids) {
        return communityBanRepository.isAnyBanned(ids, ids.size());
    }
}
