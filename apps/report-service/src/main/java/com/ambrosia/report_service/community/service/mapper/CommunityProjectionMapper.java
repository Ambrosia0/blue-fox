package com.ambrosia.report_service.community.service.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.report_service.community.entity.CommunityProjection;

@Component 
public class CommunityProjectionMapper {
    public CommunityProjection toEntity(Long communityId, CommunityCreate create){
        return CommunityProjection.create(communityId);
    }
}
