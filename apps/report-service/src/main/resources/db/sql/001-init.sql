CREATE TABLE IF NOT EXISTS community_projection(
    id bigint PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS user_projection(
    id UUID PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS post_projection(
    id bigint PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS comment_projection(
    id bigint PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS report_reason(
    id SMALLSERIAL PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS report_reason_i18n(
    report_reason_id SMALLINT REFERENCES report_reason(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    lang VARCHAR(6),
    PRIMARY KEY(report_reason_id, lang)
);

CREATE TABLE IF NOT EXISTS report(
    id UUID PRIMARY KEY DEFAULT uuidv4(),
    user_id UUID NOT NULL,
    report_reason_id SMALLINT REFERENCES report_reason(id) ON DELETE CASCADE,
    report_content TEXT NOT NULL,
    target_type TEXT NOT NULL CHECK(
        target_type IN ('POST', 'COMMENT', 'USER')
    ),
    reported_content_key TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN' CHECK(
        status IN ('OPEN', 'CLOSE')
    ),
    resolved_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_event(
    id UUID PRIMARY KEY
);