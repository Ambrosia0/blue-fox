CREATE TABLE IF NOT EXISTS service_user(
    id UUID PRIMARY KEY,
    username TEXT,
    avatar_id TEXT,
    version bigint NOT NULL,
    notification_offset TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community(
    id bigint PRIMARY KEY,
    slug TEXT,
    displayed_name TEXT,
    avatar_id TEXT,
    is_private BOOLEAN,
    deleted_at TIMESTAMP,
    version BIGINT
);

CREATE TABLE IF NOT EXISTS post(
    id BIGINT PRIMARY KEY,
    community_id BIGINT,
    is_published BOOLEAN,
    version BIGINT,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community_permission(
    user_id UUID,
    community_id BIGINT REFERENCES community(id) ON DELETE CASCADE,
    PRIMARY KEY(community_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_follow(
    user_id UUID,
    followed_user_id UUID,
    PRIMARY KEY(user_id, followed_user_id)
);

CREATE TABLE IF NOT EXISTS community_follow(
    user_id UUID,
    community_id BIGINT,
    PRIMARY KEY(user_id, community_id)
);

CREATE TABLE IF NOT EXISTS community_ban(
    user_id UUID,
    community_id BIGINT,
    before_date TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, community_id)
);

CREATE TABLE IF NOT EXISTS targeted_notification(
    id UUID PRIMARY KEY,
    user_id UUID,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_notification(
    id UUID PRIMARY KEY,
    user_id UUID,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS community_notification(
    id UUID PRIMARY KEY,
    content TEXT,
    community_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_events(
    id UUID PRIMARY KEY
);

CREATE INDEX IF NOT EXISTS user_notification_idx ON user_notification(created_at);
CREATE INDEX IF NOT EXISTS community_notification_idx ON community_notification(created_at);
CREATE INDEX IF NOT EXISTS targeted_notification_idx ON targeted_notification(created_at);

CREATE INDEX IF NOT EXISTS community_deleted_idx ON community(deleted_at);

