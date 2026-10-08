package com.ambrosia.collaboration_service.model.entity.post;

import java.util.concurrent.atomic.AtomicReference;

import com.ambrosia.collaboration_service.model.entity.Document;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor 
@Data 
public class PostDocument implements Document {
    private Long id;

    private AtomicReference<byte[]> initContent;
    
    @Override
    public Object getId() {
        return id;
    }

    public @Nullable byte[] getContent(){
        return initContent.getAndSet(null);
    }
}
