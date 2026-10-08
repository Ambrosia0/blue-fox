package com.ambrosia.content_service.post.domain.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.post.utils.ConvertedDoc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(
    access = AccessLevel.PACKAGE,
    onConstructor_ = @PersistenceCreator)
@Getter 
@Table(name = "post")
public class Post implements Serializable, Persistable<Long>{
    @Id
    private Long id;

    @Column("author_id")
    private UUID authorId;

    @Column("title")
    private String title;
    
    @Column("content")
    private String content;

    @Column("tags")
    private Set<String> tags = new HashSet<>();

    @Column("preview")
    private String preview = "";

    @Column("published")
    private boolean published = false;

    @Column("is_republished")
    private boolean isRepublished = false;

    @Column("updated_at")
    private Instant updatedAt = Instant.now();

    @Column("like_count")
    private int likeCount = 0;

    @Column("view_count")
    private long viewCount = 0L;

    @Column("comment_count")
    private int commentCount = 0;

    @Column("previewed_count")
    private long previewedCount = 0L;

    @Column("published_at")
    private Instant publishedAt;

    @ReadOnlyProperty
    @Column("created_at")
    private Instant createdAt;

    @Transient 
    private boolean isNew = true;

    private AggregateReference<Post, Long> replyId;

    private AggregateReference<CommunityProjection, Long> communityId;

    @MappedCollection(idColumn = "post_id") 
    private Set<PostCollaboration> collaborationUsers = new HashSet<>();

    @Version
    @Column("version")
    private Long version;

    @Builder 
    private Post(
            UUID authorId, 
            String title,
            Set<UUID> collaborationUsers,
            Long replyId,
            Long communityId
    ){
        this.authorId = authorId;
        this.title = title;
        if(collaborationUsers != null)
            this.collaborationUsers = collaborationUsers.stream()
                    .map(t -> new PostCollaboration(t))
                    .collect(Collectors.toSet());

        this.replyId = replyId != null?
            AggregateReference.<Post, Long>to(replyId):
            null;

        this.communityId = communityId != null?
            AggregateReference.<CommunityProjection, Long>to(communityId):
            null;
            
        this.updatedAt = Instant.now();
    }

    public void publish(){
        if(publishedAt != null)
            isRepublished = true;
        else
            publishedAt = Instant.now();
        published = true;
    }

    public void unpublish(){
        published = false;
    }

    public void editTags(Set<String> tags){
        this.tags = new HashSet<String>(tags);
    }

    public void setCollaborationUsers(Set<UUID> users){
        if(users.contains(authorId))
            throw new IllegalArgumentException("Author can't be a collaboration user!");
        this.collaborationUsers = users.stream()
            .map(t -> new PostCollaboration(t))
            .collect(Collectors.toSet());
    }

    public void editTitle(String title){
        Assert.hasLength(title, "Title must not be empty!");
        this.title = title;
    }

    public void edit(ConvertedDoc doc){
        this.content = doc.document();
        this.preview = doc.preview();
        this.updatedAt = Instant.now();
    }
}
