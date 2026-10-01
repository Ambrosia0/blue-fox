package com.ambrosia.community_service.community.service.mappers;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.model.dto.response.CommunityCreateResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityEditResponse;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.model.entity.ScopeLink;

@Component 
public class CommunityMapper {
    public CommunityCreateResponse toCreateResponse(Community community){
        return new CommunityCreateResponse(community.getId());
    }

    public CommunityEditResponse toEditResponse(Community community){
        return new CommunityEditResponse(
            community.getDisplayedName(),
            community.getDescription(),
            community.isPrivate(),
            community.getRules(),
            community.getTags(),
            community.getOwnerId(),
            community.getVersion()
        );
    }

    public Community apply(Community community, CommunityEdit communityEdit){
        if(communityEdit.description() != null) community.setDescription(communityEdit.description());
        if(communityEdit.displayedName() != null) community.setDisplayedName(communityEdit.displayedName());
        if(communityEdit.rules() != null) community.setRules(communityEdit.rules());
        if(communityEdit.tags() != null) community.setTags(communityEdit.tags());
        if(communityEdit.scopes() != null) 
            community.replaceScopes(
                communityEdit.scopes().entrySet()
                .stream()
                .<ScopeLink>mapMulti((pair, consumer) -> {
                    pair.getValue().stream()
                        .map(scope -> ScopeLink.create(pair.getKey(), scope.getId()))
                        .forEach(link -> consumer.accept(link));
                })
                .collect(Collectors.toSet())
            );
        return community;
    }
}
