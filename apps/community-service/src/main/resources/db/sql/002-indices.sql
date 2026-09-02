CREATE INDEX idx_community_owner ON community(owner_id, id);
CREATE INDEX idx_community_slug ON community(slug);

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_community_trgm_slug ON community USING gin (slug gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_community_trgm_name ON community USING gin (displayed_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_community_tags ON community USING gin (tags);