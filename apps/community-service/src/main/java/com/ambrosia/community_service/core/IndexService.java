package com.ambrosia.community_service.core;

import com.ambrosia.community_service.community.domain.entity.CommunityDelete;
import org.jspecify.annotations.NonNull;

public interface IndexService<T> {
    void index(T t);
    void reIndex(T t);
    void removeFromIndex(CommunityDelete communityDelete);
}
