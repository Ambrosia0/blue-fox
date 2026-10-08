package com.ambrosia.profile_service.blacklist.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.blacklist.api.dto.request.BlacklistRequest;
import com.ambrosia.profile_service.blacklist.domain.repository.BlacklistRepository;
import com.ambrosia.profile_service.exception.api.blacklist.MatchedIdsException;
import com.ambrosia.profile_service.util.UserCreator;

@Transactional
public class UserBlacklistIntegrationTests extends BaseIntegrationTest{
    @Autowired UserCreator userCreator;
    @Autowired UserBlacklistService userBlacklistService;
    @Autowired BlacklistRepository blacklistRepository;

    @Test
    void shouldThrowMatchedIdsException(){
        var id = UUID.randomUUID();
        assertThrows(
            MatchedIdsException.class,
            () -> userBlacklistService.blacklistUser(id, id, createRequest())
        );
    }

    @Test
    void shouldBlacklistUser(){
        var user = userCreator.create();
        var blacklistedUser = userCreator.create();
        assertDoesNotThrow(() -> userBlacklistService.blacklistUser(
            user.getId(),
            blacklistedUser.getId(), 
            createRequest()
        ));
        assertEquals(1, blacklistRepository.findByUserId(user.getId()).size());
    }

    @Test
    void shouldRemoveFromBlacklist(){
        var user = userCreator.create();
        var blaclistedUser = userCreator.create();
        assertDoesNotThrow(() -> userBlacklistService.blacklistUser(
            user.getId(),
            blaclistedUser.getId(), 
            createRequest()
        ));
        assertEquals(1, blacklistRepository.findByUserId(user.getId()).size());
        assertDoesNotThrow(() -> userBlacklistService.removeFromBlacklist(
            user.getId(),
            blaclistedUser.getId()
        ));
        assertTrue(blacklistRepository.findByUserId(user.getId()).isEmpty());
    }

    BlacklistRequest createRequest(){
        return new BlacklistRequest("Test reason");
    }
}
