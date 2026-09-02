package com.ambrosia.profile_service.user.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.profile_service.core.UserInfo;
import com.ambrosia.profile_service.core.idp.IdpUserService;
import com.ambrosia.profile_service.core.service.AvatarService;
import com.ambrosia.profile_service.core.utils.AppConfiguration;
import com.ambrosia.profile_service.exception.api.user.AvatarDoesntUploadedException;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.exception.api.user.UsernameAlreadyClaimedException;
import com.ambrosia.profile_service.exception.api.user.UsernameChangeIntervalException;
import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.model.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.model.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.model.dto.response.AvatarUploadResponse;
import com.ambrosia.profile_service.user.model.entity.UserSettings;
import com.ambrosia.profile_service.user.model.entity.UsernameHistory;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.repository.UserSettingsRepository;
import com.ambrosia.profile_service.user.repository.UsernameHistoryRepository;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.user.utils.AvatarIdGenerator;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;

    private final UserSettingsRepository userSettingsRepository;

    private final AppConfiguration appConfiguration;

    private final UsernameHistoryRepository usernameHistoryRepository;

    private final AvatarService avatarService;

    private final IdpUserService idpUserService;

    // @Override
    // public void createUnbanRequest(UUID id, String requestMsg) {
    //     var request = unbanRequestRepository.findByUserId(id);
    //     if(request.isPresent()){
    //         if(request.get().isViewed()){
    //             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is declined!");
    //         }else{
    //             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already present!");
    //         }
    //     }else{
    //         unbanRequestRepository.save(UnbanRequest.builder()
    //             .user(User.builder()
    //                 .id(id)
    //                 .build())
    //             .request(requestMsg)
    //             .isViewed(false)
    //             .build()
    //         );
    //     }
    // }

    @Override
    public void setAboutText(UUID id, String text) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new UserDoesntExistException());
        user.setAbout(text);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUsername(UUID userId, String username) {
        if (userRepository.existsByUsername(username))
            throw new UsernameAlreadyClaimedException();
        var lastAttempt = usernameHistoryRepository.findFirstByUserIdOrderByChangedAtDesc(userId);
        if (lastAttempt.isPresent() && lastAttempt.get().getChangedAt()
                                        .plus(appConfiguration.getUsernameChangeInterval())
                                        .isAfter(Instant.now()))
            throw new UsernameChangeIntervalException(
                appConfiguration.getUsernameChangeInterval(), 
                lastAttempt.get().getChangedAt());
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserDoesntExistException());
        idpUserService.updateUsername(userId, username);
        usernameHistoryRepository.save(UsernameHistory.from(user.getUsername(), userId));
    }

    @Transactional
    @Override
    public void updateFirstLastName(UUID userId, FirstLastName firstLastName) {
        if(!userRepository.existsById(userId))
            throw new UserDoesntExistException();
        idpUserService.updateFirstLastName(userId, firstLastName);
    }

    @Transactional
    @Override
    public AvatarUploadResponse updateAvatar(UUID userId, @Nullable FileMetadata fileMetadata) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserDoesntExistException());
        if(fileMetadata == null){
            avatarService.delete(userId, user.getAvatarId());
            user.setAvatarId(null);
            idpUserService.updateAvatar(userId, null);
            return null;
        }
        var avatarId = AvatarIdGenerator.generate(fileMetadata);
        return AvatarUploadResponse.from(
            avatarService.upload(userId, avatarId, fileMetadata), 
            avatarId
        );
    }

    @Override
    public void confirmAvatarUpload(UUID userId, String avatarId) {
        if(!avatarService.validateUpload(userId, avatarId))
            throw new AvatarDoesntUploadedException();
        if(!userRepository.existsById(userId))
            throw new UserDoesntExistException();
        idpUserService.updateAvatar(userId, avatarId);
    }

    @Override
    public List<UserInfo> getUserInfo(List<UUID> ids) {
        return userRepository.findByIdIn(ids);
    }

    @Override
    public void updateSettings(UUID userId, SettingsRequest settingsRequest) {
        userSettingsRepository.save(UserSettings.builder()
            .id(userId)
            .displayEmail(settingsRequest.displayEmail())
            .displayActivity(settingsRequest.displayActivity())
            .isNew(false)
            .build()
        );
    }
}
