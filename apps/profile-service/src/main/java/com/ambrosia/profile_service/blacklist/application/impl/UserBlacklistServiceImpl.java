package com.ambrosia.profile_service.blacklist.application.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.blacklist.api.dto.request.BlacklistRequest;
import com.ambrosia.profile_service.blacklist.api.dto.response.BlacklistResponse;
import com.ambrosia.profile_service.blacklist.application.UserBlacklistService;
import com.ambrosia.profile_service.blacklist.application.query.BlacklistQueryRepository;
import com.ambrosia.profile_service.blacklist.domain.repository.BlacklistRepository;
import com.ambrosia.profile_service.exception.api.blacklist.ExceededNumberOfBlacklistedException;
import com.ambrosia.profile_service.exception.api.blacklist.MatchedIdsException;
import com.ambrosia.profile_service.user.application.query.PersonalProfileInformationCache;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserBlacklistServiceImpl implements UserBlacklistService{
    private final BlacklistRepository blacklistRepository;
    private final BlacklistQueryRepository blacklistQueryRepository;

    private final PersonalProfileInformationCache personalProfileInformationCache;

    private int BLACKLIST_CONSTRAINT = 100;

    // save is atomic
    @Override
    public void blacklistUser(UUID requestingUser, UUID blacklistedUser, BlacklistRequest request) {
        if(requestingUser.equals(blacklistedUser))
            throw new MatchedIdsException();
        if(blacklistQueryRepository.getBlacklistCount(requestingUser) >= BLACKLIST_CONSTRAINT)
            throw new ExceededNumberOfBlacklistedException();
        blacklistRepository.add(requestingUser, blacklistedUser, request.reason());
        personalProfileInformationCache.evictById(requestingUser, blacklistedUser);
    }

    @Override
    public Slice<BlacklistResponse> getBlacklistedUsers(UUID requestingUser, Pageable pageable) {
        return blacklistQueryRepository.getBlacklistedUsers(requestingUser, pageable);
    }

    @Override
    public void removeFromBlacklist(UUID requestingUser, UUID blacklistedUser) {
        blacklistRepository.remove(requestingUser, blacklistedUser);
        personalProfileInformationCache.evictById(requestingUser, blacklistedUser);
    }

}
