package com.ambrosia.collaboration_service.model.entity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import com.ambrosia.collaboration_service.utils.CompactUUIDSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor 
@Getter 
@Setter  
public class User{
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarId;


    /**
     * Serialize user to raw bytes
     * @param user
     * @return serialized bytes
     */
    public byte[] serialize(){
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        byte[] firstNameBytes = firstName.getBytes(StandardCharsets.UTF_8);
        byte[] lastNameBytes = lastName.getBytes(StandardCharsets.UTF_8);
        byte[] avatarIdBytes = avatarId == null? null: avatarId.getBytes(StandardCharsets.UTF_8);

        var buf = ByteBuffer.allocate(
            CompactUUIDSerializer.UUID_SIZE+
            Integer.BYTES * 4 + 
            usernameBytes.length +
            firstNameBytes.length +
            lastNameBytes.length +
            (avatarId == null? 0: avatarIdBytes.length)
        )
            .putLong(id.getMostSignificantBits())
            .putLong(id.getLeastSignificantBits())
            .putInt(usernameBytes.length)
            .put(usernameBytes)
            .putInt(firstNameBytes.length)
            .put(firstNameBytes)
            .putInt(lastNameBytes.length)
            .put(lastNameBytes);

        if(avatarId == null)
            buf.putInt(0).put(new byte[0]);
        else
            buf.putInt(avatarIdBytes.length).put(avatarIdBytes);

        return buf.array();
    }

    public int sizeof(){
        return 
            CompactUUIDSerializer.UUID_SIZE + 
            Integer.BYTES * 4 + 
            username.getBytes(StandardCharsets.UTF_8).length + 
            firstName.getBytes(StandardCharsets.UTF_8).length + 
            lastName.getBytes(StandardCharsets.UTF_8).length + 
            (avatarId == null? 0: avatarId.getBytes(StandardCharsets.UTF_8).length);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof User other))
            return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null? Objects.hash(id): 0;
    }
}
