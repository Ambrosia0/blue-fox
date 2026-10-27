package com.ambrosia.report_service.community.service.impl;

import com.ambrosia.report_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.report_service.community.service.CommunityProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommunityProjectionServiceImpl implements CommunityProjectionService{
    private final CommunityProjectionRepository communityProjectionRepository;
    
    @Override
    public boolean exist(Long id) {
        return communityProjectionRepository.existsById(id);
    }
}
