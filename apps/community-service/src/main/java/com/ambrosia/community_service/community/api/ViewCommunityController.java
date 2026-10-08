package com.ambrosia.community_service.community.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.community.api.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.application.query.CommunitySearchService;
import com.ambrosia.community_service.community.application.query.CommunityQueryService;
import com.ambrosia.community_service.community.application.query.model.CommunityResponse;
import com.ambrosia.community_service.community.application.query.model.CommunityScoredPreview;
import com.ambrosia.library_policy.policy.Actor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/public/community")
public class ViewCommunityController {
    private final CommunityQueryService userCommunityService;

    private final CommunitySearchService communitySearchService;

    @GetMapping
    public List<CommunityScoredPreview> search(
        @ModelAttribute @Valid CommunityEventFilter eventFilter) {
        return communitySearchService.search(eventFilter, 10);
    }
    
    @GetMapping("/{slug}")
    public CommunityResponse getCommunity(
        @PathVariable String slug,
        Actor actor
    ) {
        return userCommunityService.getCommunity(slug, actor);
    }

}
