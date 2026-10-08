package com.ambrosia.community_service.utils;


import com.ambrosia.community_service.community.api.dto.request.CommunityCreate;

public class Factory {
    public static CommunityCreate createRequest(String name, String slug, boolean isPrivate){
        return new CommunityCreate(
            name,
            slug,
            isPrivate, 
            null
        );
    }
}
