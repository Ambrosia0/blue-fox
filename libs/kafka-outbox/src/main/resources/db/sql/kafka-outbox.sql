CREATE TABLE IF NOT EXISTS kafka_outbox(
    id UUID PRIMARY KEY,
    kafka_id TEXT,
    topic TEXT,
    payload BYTEA NOT NULL,
    claimed_at TIMESTAMPTZ
);