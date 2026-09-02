package com.ambrosia.profile_service.user.model.dto.request;

public record SettingsRequest(
    boolean displayEmail,
    boolean displayActivity
) {}
