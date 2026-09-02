package com.ambrosia.profile_service.user.utils;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority{
    user,
    admin;

    @Override
    public String getAuthority() {
        return this.name();
    }

    public static Role from(Collection<String> collection){
        if(collection == null)
            return null;
        for(String val: collection){
            for(Role role: values()){
                if(role.name().equalsIgnoreCase(val))
                    return role;
            }
        }
        return null;
    }
}
