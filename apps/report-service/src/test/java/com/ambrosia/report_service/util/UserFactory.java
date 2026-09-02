package com.ambrosia.report_service.util;

import java.util.UUID;

import com.ambrosia.report_service.user.entity.UserProjection;

public class UserFactory {
    public static UserProjection create(){
        return new UserProjection(UUID.randomUUID(), true);
    }
}
