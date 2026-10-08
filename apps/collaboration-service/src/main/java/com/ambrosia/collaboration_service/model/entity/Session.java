package com.ambrosia.collaboration_service.model.entity;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.ambrosia.collaboration_service.utils.CompactUUIDSerializer;

public final class Session {
    private User author;
    private Set<UUID> activeUsers;
    private Map<UUID, User> collaborators;
    private Document document;

    public Session(
            User author, 
            Collection<User> collaborators, 
            Document document
    ){
        Assert.notNull(author, "Author must not be null!");
        Assert.notEmpty(collaborators, "Collaborators must no be empty!");
        Assert.notNull(document, "Document must no be null!");

        this.collaborators = collaborators.stream()
            .collect(Collectors.toMap(k -> k.getId(), v -> v));
        this.author = author;
        this.document = document;
        this.activeUsers = Collections.synchronizedSet(new HashSet<UUID>());
    }

    public boolean addActiveUser(UUID activeUser){
        if(!collaborators.containsKey(activeUser) && !author.getId().equals(activeUser))
            throw new IllegalArgumentException("Unknown user!");
        return activeUsers.add(activeUser);
    }

    public boolean removeActiveUser(UUID activeUser){
        return activeUsers.remove(activeUser);
    }

    public List<UUID> changeCollaborators(Set<User> users){
        var delta = collaborators.entrySet()
            .stream()
            .filter(entry -> !users.contains(entry.getValue()) && !entry.getValue().equals(author))
            .map(t -> {
                var key = t.getKey();
                activeUsers.remove(key);
                return key;
            })
            .toList();
        this.collaborators = users.stream()
            .collect(Collectors.toMap(k -> k.getId(), v -> v));

        return delta;
    }
    
    public boolean hasActiveUser(UUID userId){
        return activeUsers.contains(userId);
    }

    public boolean hasCollaborationUser(UUID userId){
        return collaborators.containsKey(userId) || author.getId().equals(userId);
    }

    public User getUser(UUID userId){
        return author.getId().equals(userId)?
            author:
            collaborators.get(userId);
    }

    public Collection<User> getCollaborators(){
        return Collections.unmodifiableCollection(collaborators.values());
    }

    public User getAuthorId(){
        return this.author;
    }

    public Document getDocument(){
        return document;
    }

    public byte[] serialize(){
        var content = document.getContent();
        var buf = ByteBuffer.allocate(
            Integer.BYTES +
            author.sizeof() +
            collaborators.values().stream().mapToInt(User::sizeof).sum() +
            Integer.BYTES + 
            activeUsers.size() * CompactUUIDSerializer.UUID_SIZE +
            (content != null? content.length: 0)
        )
        .putInt(collaborators.size() + 1)
        .put(author.serialize());

        collaborators.values().forEach(t -> buf.put(t.serialize()));

        buf.putInt(activeUsers.size());

        activeUsers.forEach(t -> CompactUUIDSerializer.serializer(t));

        if(content != null)
            buf.put(content);
        
        return buf.array();
    }
}
