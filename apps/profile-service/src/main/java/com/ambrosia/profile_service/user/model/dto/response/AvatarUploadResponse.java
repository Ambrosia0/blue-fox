package com.ambrosia.profile_service.user.model.dto.response;

public record AvatarUploadResponse(
    String uploadUrl,
    String avatarId
) {
    public static AvatarUploadResponse from(String uploadUrl, String avatarId){
        return new AvatarUploadResponse(uploadUrl, avatarId);
    }
}
