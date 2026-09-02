package com.ambrosia.content_service.post.model.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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

    @JsonInclude(value = Include.NON_NULL)
    private List<String> tags;
    
    private Instant publishedAt;

    @JsonInclude(value = Include.NON_NULL)
    private Long communityId;

    @JsonInclude(value = Include.NON_NULL)
    private Boolean isLiked;

    private int likeCount;
    private int commentCount;
    private long viewCount;
    private long previewedCount;

    @JsonInclude(value = Include.NON_NULL)
    private String communityName;

    @JsonInclude(value = Include.NON_NULL)
    private Boolean isCommunityPrivate;

    @JsonInclude(value = Include.NON_NULL)
    private UUID communityAvatarId;

}
