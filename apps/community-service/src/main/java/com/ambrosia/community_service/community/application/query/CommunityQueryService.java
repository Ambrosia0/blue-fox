package com.ambrosia.community_service.community.application.query;

import com.ambrosia.community_service.community.application.query.model.CommunityResponse;
import com.ambrosia.library_policy.policy.Actor;

public interface CommunityQueryService {
    CommunityResponse getCommunity(String slug, Actor actor);
}
