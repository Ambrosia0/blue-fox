package com.ambrosia.community_service.community.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ScopeEnum {
    POST_DELETE((short)1, "Delete posts"),
    COMMENT_DELETE((short)2, "Delete comments"),
    USER_BAN((short)3, "Ban users"),
    USER_UNBAN((short)4, "Unban users"),
    FOLLOW_MANAGE((short)5, "Follow managing");

    private static final Map<Short, ScopeEnum> BY_ID = Arrays.stream(ScopeEnum.values())
        .collect(Collectors.toUnmodifiableMap(
            ScopeEnum::getId,
            Function.identity()
        ));

    ScopeEnum(Short id, String name){
        this.id = id;
        this.name = name;
    }

    private final Short id;
    private final String name;

    public Short getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public static ScopeEnum fromId(Short id){
        var scope = BY_ID.get(id);
        if(scope == null){
            throw new IllegalArgumentException("Unknown scope id:"+id);
        }
        return scope;
    }
}
