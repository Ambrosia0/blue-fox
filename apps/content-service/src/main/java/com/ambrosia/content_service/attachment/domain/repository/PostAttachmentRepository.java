package com.ambrosia.content_service.attachment.domain.repository;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.attachment.domain.entity.PostAttachment;

public interface PostAttachmentRepository {
    boolean existsByAuthorIdAndAttachmentId(UUID requestingUser, String attachmentId);
    List<String> findAttachmentIdsByPostIdAndAuthorId(Long postId, UUID authorId);
    boolean deleteByAuthorIdAndAttachmentIdAndPostId(UUID authorId, String attachmentId, Long postId);

    int deletionMark(UUID authorId, Long postId, String attachmentId);
    int deletionMarkAll(UUID authorId, Long postId);

    List<PostAttachment> findAllDeletable(long limit);
    List<PostAttachment> findByPostId(Long postId);

    PostAttachment save(PostAttachment postAttachment);
}
