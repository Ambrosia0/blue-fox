package com.ambrosia.community_service.community.model.dto.response;

public record AvatarUploadResponse(
    String uploadUrl,
    String avatarId
) {
    public static AvatarUploadResponse from(String uploadUrl, String avatarId){
        return new AvatarUploadResponse(uploadUrl, avatarId);
    }
}
