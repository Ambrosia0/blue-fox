package com.ambrosia.profile_service.user.api.dto.request;

public record SettingsRequest(
    boolean displayEmail,
    boolean displayActivity
) {}
