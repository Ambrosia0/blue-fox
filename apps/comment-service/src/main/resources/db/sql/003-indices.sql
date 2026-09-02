CREATE INDEX IF NOT EXISTS comment_idx ON comment(created_at, id);
CREATE INDEX IF NOT EXISTS comment_tree_idx ON comment(parent_comment_id);

CREATE INDEX IF NOT EXISTS attachment_comment_idx ON comment_attachment(comment_id);
CREATE INDEX IF NOT EXISTS attachment_delete_idx ON comment_attachment(claimed_at) WHERE to_delete = 'true';