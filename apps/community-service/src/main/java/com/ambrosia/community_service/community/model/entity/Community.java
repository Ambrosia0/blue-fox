package com.ambrosia.community_service.community.model.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.community.utils.ScopeEnum;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Table(name = "community")
@AllArgsConstructor 
@Data 
public class Community implements Serializable{
    @Id
    private Long id;

    @Column(value = "slug")
    private String slug;

    @Column(value = "displayed_name")
    private String displayedName;

    @Setter(value = AccessLevel.NONE)
    @Column(value = "owner_id")
    private UUID ownerId;

    @Column(value = "avatar_id")
    private String avatarId;

    @Column(value = "description")
    private String description = "";

    @Column(value = "is_private")
    private boolean isPrivate = false;

    @Column(value = "follow_count")
    private Long followCount = 0L;

    @Column(value = "post_count")
    private Long postCount = 0L;
    
    @Column(value = "rules")
    private List<String> rules;

    @Column(value = "tags")
    private Set<String> tags;

    @Version
    @Column(value = "version")
    private Long version;

    @ReadOnlyProperty
    @Column(value = "created_at")
    private Instant createdAt;

    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    @MappedCollection(idColumn = "community_id")
    private Set<ScopeLink> scopes = new HashSet<>();

    public void setOwnerId(@Nullable UUID ownerId){
        var newScopes = scopes.stream()
            .filter(t -> !Objects.equals(t.getUserId(), this.ownerId))
            .collect(Collectors.toSet());
        newScopes.addAll(ScopeLink.create(ownerId, ScopeEnum.values()));
        this.scopes = newScopes;
        this.ownerId = ownerId;
    }

    public Set<ScopeLink> getScopes(){
        return Collections.unmodifiableSet(scopes);
    }

    public void replaceScopes(Set<ScopeLink> scopes){
        var newScopes = new HashSet<>(scopes);
        if(ownerId != null){
            newScopes.addAll(ScopeLink.create(ownerId, ScopeEnum.values()));
        }
        this.scopes = newScopes;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private Long id;
        private String slug;
        private String displayedName;
        private UUID ownerId;
        private String avatarId;
        private String description = "";
        private boolean isPrivate = false;
        private Long followCount = 0L;
        private Long postCount = 0L;
        private List<String> rules;
        private Set<String> tags;
        private Long version;
        private Instant createdAt;
        private Set<ScopeLink> scopes = new HashSet<>();

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder slug(String slug){
            this.slug = slug;
            return this;
        }

        public Builder displayedName(String displayedName){
            this.displayedName = displayedName;
            return this;
        }

        public Builder ownerId(UUID ownerId){
            this.ownerId = ownerId;
            return this;
        }

        public Builder avatarId(String avatarId){
            this.avatarId = avatarId;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public Builder isPrivate(boolean isPrivate){
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder followCount(Long followCount){
            this.followCount = followCount;
            return this;
        }

        public Builder postCount(Long postCount){
            this.postCount = postCount;
            return this;
        }

        public Builder rules(List<String> rules){
            this.rules = rules != null? 
                new ArrayList<>(rules):
                null;
            return this;
        }

        public Builder tags(Set<String> tags){
            this.tags = tags != null?
                new HashSet<>(tags):
                null;
            return this;
        }

        public Builder version(Long version){
            this.version = version;
            return this;
        }

        public Builder scopes(Set<ScopeLink> scopes){
            this.scopes = scopes;
            return this;
        }

        private static Set<ScopeLink> normalizeScopes(UUID ownerId, Set<ScopeLink> scopes){
            var normalized = scopes == null?
                new HashSet<ScopeLink>():
                new HashSet<ScopeLink>(scopes);
            if(ownerId != null)
                normalized.addAll(ScopeLink.create(ownerId, ScopeEnum.values()));
            return normalized;
        }

        public Community build(){
            return new Community(
                id, 
                slug, 
                displayedName, 
                ownerId, 
                avatarId, 
                description, 
                isPrivate, 
                followCount, 
                postCount, 
                rules, 
                tags, 
                version, 
                createdAt, 
                normalizeScopes(ownerId, scopes)
            );
        }
    }
}
