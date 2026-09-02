CREATE TABLE IF NOT EXISTS search_index_outbox(
    id UUID PRIMARY KEY,
    resource_id TEXT NOT NULL,
    entity_type TEXT NOT NULL,
    payload TEXT,
    claimed_at TIMESTAMPTZ
);