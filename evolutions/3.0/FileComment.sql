


drop table File_Comment;
 
CREATE TABLE File_Comment
(
 id bigserial NOT NULL,
 comment text,
 comment_By_id bigint,
 upload_File_Info_Id bigint NOT NULL,
 message_id bigint NOT NULL unique,
 created_on timestamp without time zone NOT NULL,
 last_update timestamp without time zone NOT NULL,
 
 CONSTRAINT pk_File_Comment PRIMARY KEY (id),
 
 CONSTRAINT fk_comment_By_id FOREIGN KEY (comment_By_id)
     REFERENCES app_user(id) MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION,

     CONSTRAINT fk_upload_File_Info_Id FOREIGN KEY (upload_File_Info_Id)
     REFERENCES upload_File_Info(id) MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION,

     CONSTRAINT fk_File_Commentt_Message_id FOREIGN KEY (message_id)
     REFERENCES Message (id) MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE INDEX ix_File_Comment
  ON File_Comment
  USING btree
  (id);

  CREATE INDEX ix_upload_File_Info_Id
  ON File_Comment
  USING btree
  (upload_File_Info_Id);


CREATE INDEX ix_message_By
  ON File_Comment
  USING btree
  (comment_By_id);

  CREATE INDEX ix_message_File_Comment
  ON File_Comment
  USING btree
  (id);
  

  CREATE SEQUENCE File_Comment_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  ALTER TABLE attachment
  OWNER TO postgres;

  