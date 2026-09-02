package com.ambrosia.profile_service.exception.api.user;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;

import com.ambrosia.profile_service.exception.ApiException;

import lombok.Getter;

@Getter
public class UsernameChangeIntervalException extends ApiException{
    public UsernameChangeIntervalException(Duration duration, Instant lastAttempt){
        super(HttpStatus.BAD_REQUEST, 
            String.format("Usernames can be changed every %s, last attempt %s",
                duration,
                lastAttempt
            ));
    }
}
