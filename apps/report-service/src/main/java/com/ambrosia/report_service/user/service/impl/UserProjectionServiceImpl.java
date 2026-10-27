package com.ambrosia.report_service.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import com.ambrosia.report_service.user.service.UserProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProjectionServiceImpl implements UserProjectionService{
    private final UserProjectionRepository userProjectionRepository;

    @Override
    public boolean exist(UUID id) {
        return userProjectionRepository.existsById(id);
    }
}
