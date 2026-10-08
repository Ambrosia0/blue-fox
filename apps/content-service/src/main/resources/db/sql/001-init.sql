CREATE TABLE IF NOT EXISTS community_projection(
    id bigint PRIMARY KEY,
    name TEXT NOT NULL,
    slug TEXT UNIQUE NOT NULL,
    is_private BOOLEAN NOT NULL,
    avatar_id TEXT
);

CREATE TABLE IF NOT EXISTS user_projection(
    id UUID PRIMARY KEY,
    username TEXT NOT NULL,
    first_name TEXT,
    last_name TEXT,
    avatar_id TEXT
);

CREATE TABLE IF NOT EXISTS post(
    id BIGSERIAL PRIMARY KEY,
    author_id UUID REFERENCES user_projection(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    content TEXT CHECK(char_length(content) < 20000),
    preview TEXT,
    tags TEXT[],
    community_id BIGINT REFERENCES community_projection(id) ON DELETE SET NULL,
    like_count integer NOT NULL DEFAULT 0,
    view_count bigint NOT NULL DEFAULT 0,
    previewed_count bigint NOT NULL DEFAULT 0,
    comment_count integer NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT 'false',
    is_republished BOOLEAN NOT NULL DEFAULT 'false',
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    published_at timestamp DEFAULT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    reply_id BIGINT REFERENCES post(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS document_vector(
    id bigint REFERENCES post(id) ON DELETE CASCADE,
    search_vector tsvector NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS post_like(
    user_id UUID REFERENCES user_projection(id) ON DELETE CASCADE,
    post_id BIGINT REFERENCES post(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, post_id)
);

CREATE TABLE IF NOT EXISTS post_attachment(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id bigint REFERENCES post(id) ON DELETE SET NULL,
    attachment_id text NOT NULL,
    to_delete boolean NOT NULL DEFAULT 'false',
    claimed_at TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS user_follow(
    user_id UUID REFERENCES user_projection ON DELETE CASCADE,
    followed_user_id UUID REFERENCES user_projection ON DELETE CASCADE,
    followed_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(user_id, followed_user_id)
);

CREATE TABLE IF NOT EXISTS community_follow_projection(
    user_id UUID NOT NULL,
    community_id BIGINT NOT NULL,
    PRIMARY KEY(user_id, community_id)
);

CREATE TABLE IF NOT EXISTS community_ban_projection(
    community_id bigint NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY(user_id, community_id)
);

CREATE TABLE IF NOT EXISTS processed_events(
    id UUID PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS post_collaboration(
    user_id UUID NOT NULL,
    post_id BIGINT REFERENCES post(id) ON DELETE CASCADE,
    PRIMARY KEY(post_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_permission(
    community_id BIGINT REFERENCES community_projection(id) ON DELETE CASCADE,
    permission TEXT,
    user_id UUID REFERENCES user_projection(id) ON DELETE CASCADE,
    PRIMARY KEY(community_id, user_id, permission)
);