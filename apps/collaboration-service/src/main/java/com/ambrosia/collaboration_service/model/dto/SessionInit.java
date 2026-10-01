package com.ambrosia.collaboration_service.model.dto;

import java.util.List;

import com.ambrosia.collaboration_service.model.entity.User;

public record SessionInit(
    User author,
    List<User> collaborators
) {}
