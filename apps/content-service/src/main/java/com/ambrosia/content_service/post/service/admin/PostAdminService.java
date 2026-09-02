package com.ambrosia.content_service.post.service.admin;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;

public interface PostAdminService {
    PostContentResponse getPost(long id);
}
