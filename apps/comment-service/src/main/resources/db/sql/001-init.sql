CREATE TABLE IF NOT EXISTS community_projection(
    id bigint PRIMARY KEY,
    is_private boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS post_projection(
    post_id bigint PRIMARY KEY,
    community_id bigint REFERENCES community_projection(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comment(
    id bigserial PRIMARY KEY,
    post_id bigint REFERENCES post_projection(post_id) ON DELETE CASCADE,
    user_id UUID,
    content text,
    parent_comment_id bigint, -- null if root
    like_count integer DEFAULT 0,
    number_of_children integer DEFAULT 0,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    is_visible boolean default 'true',
    CONSTRAINT fk_comment_tree FOREIGN KEY (parent_comment_id) 
        REFERENCES comment(id)
);

CREATE TABLE IF NOT EXISTS comment_attachment(
    attachment_id TEXT PRIMARY KEY,
    comment_id bigint REFERENCES comment(id) ON DELETE SET NULL,
    to_delete boolean NOT NULL DEFAULT 'false',
    claimed_at TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS comment_like(
    comment_id bigint REFERENCES comment(id) ON DELETE CASCADE,
    user_id UUID,
    PRIMARY KEY(comment_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_ban_projection(
    community_id bigint NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY(user_id, community_id)
);

CREATE TABLE IF NOT EXISTS community_follow_projection(
    user_id UUID NOT NULL,
    community_id BIGINT NOT NULL,
    PRIMARY KEY(user_id, community_id)
);

CREATE TABLE IF NOT EXISTS processed_event(
    id UUID PRIMARY KEY
);