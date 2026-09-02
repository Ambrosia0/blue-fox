CREATE TABLE IF NOT EXISTS community(
    id BIGSERIAL PRIMARY KEY,
    slug TEXT CHECK(char_length(slug) < 32 AND char_length(slug) >= 6) UNIQUE NOT NULL,
    displayed_name TEXT NOT NULL,
    owner_id UUID,
    avatar_id TEXT,
    description TEXT,
    follow_count bigint NOT NULL DEFAULT 0,
    post_count bigint NOT NULL DEFAULT 0,
    is_private BOOLEAN NOT NULL DEFAULT 'false',
    tags TEXT[],
    rules TEXT[],
    version BIGINT NOT NULL DEFAULT 0,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS scope(
    id SMALLINT PRIMARY KEY,
    scope_type VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS scope_link(
    user_id UUID NOT NULL,
    scope_id SMALLINT REFERENCES scope(id),
    community_id BIGINT REFERENCES community(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, community_id, scope_id)
);

CREATE TABLE IF NOT EXISTS community_ban(
    user_id UUID NOT NULL,
    community_id BIGINT REFERENCES community(id) ON DELETE CASCADE,
    before_date timestamp CHECK(before_date IS NULL OR before_date > CURRENT_TIMESTAMP),
    PRIMARY KEY(user_id, community_id, before_date)
);

CREATE TABLE IF NOT EXISTS community_follow(
    user_id UUID NOT NULL,
    community_id BIGINT REFERENCES community(id) ON DELETE CASCADE,
    followed_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(user_id, community_id)
);

CREATE TABLE IF NOT EXISTS community_follow_request(
    user_id UUID NOT NULL,
    community_id BIGINT REFERENCES community(id) ON DELETE CASCADE,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(user_id, community_id)
);