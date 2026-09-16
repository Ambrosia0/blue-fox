package com.ambrosia.content_service.post.model.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
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
    private List<String> tags;

    @Builder.Default
    @Column("preview")
    private String preview = "";

    @Builder.Default
    @Column("published")
    private boolean published = false;

    @Builder.Default
    @Column("updated_at")
    private Instant updatedAt = Instant.now();

    @Builder.Default
    @Column("like_count")
    private int likeCount = 0;

    @Builder.Default
    @Column("view_count")
    private long viewCount = 0L;

    @Builder.Default
    @Column("comment_count")
    private int commentCount = 0;

    @Builder.Default
    @Column("previewed_count")
    private long previewedCount = 0L;

    @Column("published_at")
    private Instant publishedAt;

    @ReadOnlyProperty
    @Column("created_at")
    private Instant createdAt;

    @Builder.Default
    @Column("visible")
    private boolean visible = true;

    @Builder.Default
    @Transient 
    private boolean isNew = false;

    private AggregateReference<Post, Long> replyId;

    private AggregateReference<CommunityProjection, Long> communityId;

    @Version
    @Column("version")
    private Long version;
}
