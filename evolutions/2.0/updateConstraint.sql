


   ALTER TABLE file_comment drop  CONSTRAINT fk_file_commentt_message_id;

   ALTER TABLE file_comment
   ADD CONSTRAINT fk_file_commentt_message_id FOREIGN KEY (message_id)
   REFERENCES message (id) MATCH SIMPLE
   ON UPDATE NO ACTION ON DELETE CASCADE;




   ALTER TABLE file_comment drop  CONSTRAINT fk_upload_file_info_id;

    ALTER TABLE file_comment
    ADD CONSTRAINT fk_upload_file_info_id FOREIGN KEY (upload_file_info_id)
    REFERENCES upload_file_info (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE CASCADE;
    
    
    CREATE OR REPLACE FUNCTION removedatatomsg() RETURNS TRIGGER AS $upload_file_info_trigger$
   BEGIN
       delete from message where id =old.message_id;
      RETURN OLD;
   END;
  $upload_file_info_trigger$ LANGUAGE plpgsql;


  CREATE TRIGGER upload_file_info_trigger BEFORE DELETE ON upload_file_info
  FOR EACH ROW EXECUTE PROCEDURE removedatatomsg()
  
  
   ALTER TABLE notification drop  CONSTRAINT fk_notification_message_40;
  
  
  ALTER TABLE notification
  ADD CONSTRAINT fk_notification_message_40 FOREIGN KEY (message_id)
      REFERENCES message (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;
      