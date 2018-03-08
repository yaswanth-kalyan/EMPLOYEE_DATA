

 alter table Message DROP COLUMN url;

  alter table Message DROP COLUMN icon;

  ALTER TABLE MESSAGE ADD random_id CHARACTER VARYING(255);

  ALTER TABLE MESSAGE ADD  CONSTRAINT  uq_message_random_id unique (random_id);
  
  
  CREATE INDEX ix_message_chatgroup_26
  ON message
  USING btree
  (chat_group_id);
  
  CREATE INDEX ix_message_messageby_24
  ON message
  USING btree
  (message_by_id);
  
  
  CREATE INDEX ix_message_messageto_25
  ON message
  USING btree
  (message_to_id);
  
  CREATE INDEX ix_message_message_description_25
  ON message
  USING btree
  (description);
  
  
  CREATE INDEX ix_message_created_on_25
  ON message
  USING btree
  (created_on);
  
  CREATE INDEX ix_message_random_id_25
  ON message
  USING btree
  (random_id);
  
  CREATE INDEX ix_message_role_25
  ON message
  USING btree
  (role);
  
  CREATE INDEX ix_message_message_content_type_25
  ON message
  USING btree
  (message_content_type);
  
  
  CREATE INDEX ix_chat_group_createdby_6
  ON chat_group
  USING btree
  (created_by_id);
  
  
  
  
  
 CREATE INDEX ix_chat_group_created_on_6
  ON chat_group
  USING btree
  (created_on);
    CREATE INDEX ix_chat_name_6
  ON chat_group
  USING btree
  (name );
  
  
  CREATE INDEX ix_upload_file_info_appuser_39
  ON upload_file_info
  USING btree
  (app_user_id);
  
  
  CREATE INDEX ix_upload_file_info_message_40
  ON upload_file_info
  USING btree
  (message_id);
  
  CREATE INDEX ix_chat_app_user_last_seen_tab_5
  ON chat_app_user_last_seen_tab_info
  USING btree
  (logged_in_user_id);
  
  
  
  