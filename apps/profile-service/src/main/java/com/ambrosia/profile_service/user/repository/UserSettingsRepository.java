package com.ambrosia.profile_service.user.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.ambrosia.profile_service.user.model.entity.UserSettings;

public interface UserSettingsRepository extends CrudRepository<UserSettings, UUID>{}
