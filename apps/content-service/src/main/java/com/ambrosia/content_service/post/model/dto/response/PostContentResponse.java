package com.ambrosia.content_service.post.model.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PostContentResponse implements Serializable{
    private Long id;
    private UUID authorId;
    private String title;
    private String content;
    private String preview;

    private List<String> tags;
    
    private Instant publishedAt;

    private Boolean isLiked;

    private int likeCount;
    private int commentCount;
    private long viewCount;
    private long previewedCount;

    private PostResponse response;
    private CommunityResponse community;
}
