CREATE OR REPLACE FUNCTION mark_attachments_for_deletion()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE comment_attachment
    SET to_delete = 'true'
    WHERE comment_id = OLD.id;
    RETURN OLD;
END;
$$;
//

CREATE TRIGGER comment_delete_attachments
AFTER DELETE ON comment
FOR EACH ROW
EXECUTE FUNCTION mark_attachments_for_deletion();
//