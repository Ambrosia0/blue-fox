package com.ambrosia.profile_service.user.model.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Transient;

import com.ambrosia.profile_service.user.model.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Projection of 
 * @see User
 */
@AllArgsConstructor
@Getter
@Setter
public class PublicUserProfileResponse implements Serializable{
    private UUID id;

    private String username;

    @JsonInclude(value = Include.NON_NULL)
    private String firstName;

    @JsonInclude(value = Include.NON_NULL)
    private String lastName;

    private String about;

    @JsonInclude(value = Include.NON_NULL)
    private String email;

    @JsonInclude(value = Include.NON_NULL)
    private String status;

    @JsonInclude(value = Include.NON_NULL)
    private Instant lastActivity;

    private UUID avatarId;
    private long followCount;
    private Instant createdAt;

    @Transient
    @JsonInclude(value = Include.NON_NULL)
    @JsonUnwrapped
    private ProfileUserData userData;
}
