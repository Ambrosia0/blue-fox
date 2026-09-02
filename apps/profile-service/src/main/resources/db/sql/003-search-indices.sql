CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_user_trgm ON service_user USING gin (username gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_user_first_name_trgm ON service_user USING gin (first_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_user_last_name_trgm ON service_user USING gin (last_name gin_trgm_ops);