package com.ambrosia.collaboration_service.utils;

import com.ambrosia.collaboration_service.model.entity.message.Message;

public interface SerializerRegistry {
    Message serialize(Object obj);    
}
