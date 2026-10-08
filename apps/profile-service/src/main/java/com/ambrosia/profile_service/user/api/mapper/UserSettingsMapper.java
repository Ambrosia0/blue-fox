package com.ambrosia.profile_service.user.api.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.user.api.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.domain.entity.UserSettings;

@Component 
public class UserSettingsMapper {
    public UserSettings toEntity(SettingsRequest settingsRequest){
        return UserSettings.builder()
            .displayActivity(settingsRequest.displayActivity())
            .displayEmail(settingsRequest.displayEmail())
            .build();
    }
}
