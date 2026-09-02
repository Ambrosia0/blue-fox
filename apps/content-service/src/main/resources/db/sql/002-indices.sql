CREATE EXTENSION IF NOT EXISTS rum;

CREATE INDEX IF NOT EXISTS idx_post_latest ON post(published_at, id);
CREATE INDEX IF NOT EXISTS idx_rum_document ON document_vector USING rum (search_vector rum_tsvector_ops);
CREATE INDEX IF NOT EXISTS idx_gin_tags ON post USING gin (tags);

CREATE INDEX IF NOT EXISTS idx_post_attachment ON post_attachment(post_id);
CREATE INDEX IF NOT EXISTS idx_post_attachment_delete ON post_attachment(claimed_at) WHERE to_delete = 'true';