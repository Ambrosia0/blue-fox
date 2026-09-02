CREATE TABLE IF NOT EXISTS service_user(
    id UUID PRIMARY KEY,
    username text UNIQUE,
    first_name text,
    last_name text,
    about text CHECK(char_length(about) < 500) NOT NULL DEFAULT '',
    user_role text DEFAULT 'user',
    is_active boolean DEFAULT 'yes',
    is_enabled boolean DEFAULT 'no',
    email text UNIQUE NOT NULL,
    follow_count bigint NOT NULL DEFAULT 0,
    blacklist_count SMALLINT NOT NULL DEFAULT 0 CHECK(blacklist_count BETWEEN 0 AND 100),
    status TEXT NOT NULL CHECK(
        status IN ('ONLINE', 'OFFLINE')
    ),
    last_activity timestamp DEFAULT CURRENT_TIMESTAMP,
    avatar_id text,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS unban_request(
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES service_user(id) UNIQUE,
    request text CHECK(char_length(request) < 1000 AND char_length(request) > 20),
    is_viewed boolean DEFAULT 'no',
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS username_history(
    id UUID PRIMARY KEY DEFAULT uuidv4(),
    username text,
    user_id UUID REFERENCES service_user(id) ON DELETE CASCADE,
    changed_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_settings(
    user_id UUID REFERENCES service_user(id) ON DELETE CASCADE,
    display_email boolean DEFAULT 'no' NOT NULL,
    display_activity boolean DEFAULT 'yes' NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE IF NOT EXISTS blacklist(
    user_id UUID REFERENCES service_user(id) ON DELETE CASCADE,
    blacklisted_user_id UUID REFERENCES service_user(id) ON DELETE CASCADE,
    reason TEXT,
    CHECK (user_id != blacklisted_user_id),
    PRIMARY KEY (user_id, blacklisted_user_id)
);