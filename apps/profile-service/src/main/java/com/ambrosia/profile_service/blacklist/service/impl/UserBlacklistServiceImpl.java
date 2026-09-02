package com.ambrosia.profile_service.blacklist.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.blacklist.model.dto.request.BlacklistRequest;
import com.ambrosia.profile_service.blacklist.model.dto.response.BlacklistResponse;
import com.ambrosia.profile_service.blacklist.model.entity.Blacklist;
import com.ambrosia.profile_service.blacklist.model.entity.key.BlacklistKey;
import com.ambrosia.profile_service.blacklist.repository.BlacklistQueryRepository;
import com.ambrosia.profile_service.blacklist.repository.BlacklistRepository;
import com.ambrosia.profile_service.blacklist.service.UserBlacklistService;
import com.ambrosia.profile_service.exception.api.blacklist.ExceededNumberOfBlacklistedException;
import com.ambrosia.profile_service.exception.api.blacklist.MatchedIdsException;
import com.ambrosia.profile_service.user.service.cache.PersonalProfileInformationCache;

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
        blacklistRepository.save(Blacklist.create(
            requestingUser, 
            blacklistedUser,
            request.reason()
        ));
        personalProfileInformationCache.evictById(requestingUser, blacklistedUser);
    }

    @Override
    public Slice<BlacklistResponse> getBlacklistedUsers(UUID requestingUser, Pageable pageable) {
        return blacklistQueryRepository.getBlacklistedUsers(requestingUser, pageable);
    }

    @Override
    public void removeFromBlacklist(UUID requestingUser, UUID blacklistedUser) {
        blacklistRepository.deleteById(BlacklistKey.from(requestingUser, blacklistedUser));
        personalProfileInformationCache.evictById(requestingUser, blacklistedUser);
    }

}
