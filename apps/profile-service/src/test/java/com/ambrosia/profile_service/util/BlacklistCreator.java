package com.ambrosia.profile_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.profile_service.blacklist.model.entity.Blacklist;
import com.ambrosia.profile_service.blacklist.repository.BlacklistRepository;

@TestComponent
public class BlacklistCreator {
    @Autowired UserCreator userCreator;
    @Autowired BlacklistRepository blacklistRepository;

    public Blacklist createFromScratch(){
        var user1 = userCreator.createFromScratch();
        var user2 = userCreator.createFromScratch();
        return blacklistRepository.save(Blacklist.create(
            user1.getId(),
            user2.getId(),
            "Test reason"
        ));
    }

    public void cleanUp(){
        blacklistRepository.deleteAll();
    }
}
