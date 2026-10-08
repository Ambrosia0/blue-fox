package com.ambrosia.profile_service.user.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
import com.ambrosia.profile_service.exception.api.user.AvatarDoesntUploadedException;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.user.api.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.api.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.api.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.api.dto.response.AvatarUploadResponse;
import com.ambrosia.profile_service.user.api.mapper.UserSettingsMapper;
import com.ambrosia.profile_service.user.application.AvatarService;
import com.ambrosia.profile_service.user.application.IdpUserService;
import com.ambrosia.profile_service.user.application.UserProfileService;
import com.ambrosia.profile_service.user.domain.entity.UsernameHistory;
import com.ambrosia.profile_service.user.domain.policy.UsernameChangePolicy;
import com.ambrosia.profile_service.user.domain.repository.UserRepository;
import com.ambrosia.profile_service.user.domain.repository.UsernameHistoryRepository;
import com.ambrosia.profile_service.user.utils.AvatarIdGenerator;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;

    private final UsernameHistoryRepository usernameHistoryRepository;

    private final AvatarService avatarService;

    private final IdpUserService idpUserService;

    private final UserSettingsMapper userSettingsMapper;

    private final GenericPolicyContext genericPolicyContext;

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
    public void setAboutText(Actor actor, String text) {
        var user = userRepository.findById(actor.id())
            .orElseThrow(() -> new UserDoesntExistException());
        user.setAbout(text);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUsername(Actor actor, String username) {
        genericPolicyContext.evaluate(
            actor, 
            UsernameChangePolicy.class, 
            username
        );
        var user = userRepository.findById(actor.id())
            .orElseThrow(() -> new UserDoesntExistException());
        idpUserService.updateUsername(actor.id(), username);
        usernameHistoryRepository.save(UsernameHistory.from(user.getUsername(), actor.id()));
    }

    @Transactional
    @Override
    public void updateFirstLastName(Actor actor, FirstLastName firstLastName) {
        if(!userRepository.existsById(actor.id()))
            throw new UserDoesntExistException();
        idpUserService.updateFirstLastName(actor.id(), firstLastName);
    }

    @Transactional
    @Override
    public AvatarUploadResponse updateAvatar(Actor actor, @Nullable FileMetadata fileMetadata) {
        var user = userRepository.findById(actor.id())
            .orElseThrow(() -> new UserDoesntExistException());
        if(fileMetadata == null){
            avatarService.delete(actor.id(), user.getAvatarId());
            user.setAvatarId(null);
            idpUserService.updateAvatar(actor.id(), null);
            return null;
        }
        var avatarId = AvatarIdGenerator.generate(fileMetadata);
        return AvatarUploadResponse.from(
            avatarService.upload(actor.id(), avatarId, fileMetadata), 
            avatarId
        );
    }

    @Override
    public void confirmAvatarUpload(Actor actor, String avatarId) {
        if(!avatarService.validateUpload(actor.id(), avatarId))
            throw new AvatarDoesntUploadedException();
        if(!userRepository.existsById(actor.id()))
            throw new UserDoesntExistException();
        idpUserService.updateAvatar(actor.id(), avatarId);
    }

    @Override
    public void updateSettings(Actor actor, SettingsRequest settingsRequest) {
        var user = userRepository.findById(actor.id())
            .orElseThrow(() -> new UserDoesntExistException());
        user.changeSettings(userSettingsMapper.toEntity(settingsRequest));
        userRepository.save(user);
    }
}
