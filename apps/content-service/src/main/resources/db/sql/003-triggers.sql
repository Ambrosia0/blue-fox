CREATE OR REPLACE FUNCTION mark_attachments_for_deletion()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE post_attachment
    SET to_delete = 'true'
    WHERE post_id = OLD.id;
    RETURN OLD;
END;
$$;
//

CREATE TRIGGER post_delete_attachments
AFTER DELETE ON post
FOR EACH ROW
EXECUTE FUNCTION mark_attachments_for_deletion();
//