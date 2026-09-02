package com.ambrosia.community_service.community.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;
import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.service.CommunitySearchService;
import com.ambrosia.community_service.community.service.UserCommunityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/community")
public class PublicCommunityController {
    private final UserCommunityService userCommunityService;

    private final CommunitySearchService communitySearchService;

    @GetMapping
    public List<CommunityPreview> search(
        @ModelAttribute @Valid CommunityEventFilter eventFilter) {
        return communitySearchService.search(eventFilter, 10);
    }
    
    @GetMapping("/{slug}")
    public CommunityResponse getCommunity(
        @PathVariable String slug,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return userCommunityService.getCommunity(
            slug,
            jwt != null?
                UUID.fromString(jwt.getSubject()):
                null
        );
    }

}
