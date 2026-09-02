package com.ambrosia.profile_service.user.model.dto.response;

import java.io.Serializable;

public record ProfileSettingsResponse(
    boolean displayEmail,
    boolean displayActivity
) implements Serializable {}
