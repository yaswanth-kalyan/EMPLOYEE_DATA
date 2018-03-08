 
 
CREATE TABLE Attachment
(
 id bigserial NOT NULL,
 attachment_Image bytea,
 description text,
 title text,
 url text,
 attachment_Type character varying(50),
 app_user_id bigint NOT NULL,
 created_on timestamp without time zone NOT NULL,
 last_update timestamp without time zone NOT NULL,
 CONSTRAINT pk_Attachment PRIMARY KEY (id),
 
 CONSTRAINT fk_Attachment_appuser_45 FOREIGN KEY (app_user_id)
     REFERENCES app_user (id) MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT ck_Attachment_type CHECK (attachment_Type::text = ANY (ARRAY['URL'::character varying, 'TEXTFILE'::character varying]))
);


CREATE INDEX ix_Attachment_30
  ON Attachment
  USING btree
  (id);

  CREATE SEQUENCE attachment_id_seq1
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  ALTER TABLE attachment
  OWNER TO postgres;

  
  
CREATE TABLE Message_Attachment
(
 id bigserial NOT NULL,
 message_id bigint NOT NULL,
 Attachment_id bigint NOT NULL,
 created_on timestamp without time zone NOT NULL,
 last_update timestamp without time zone NOT NULL,
 CONSTRAINT pk_Message_Attachment PRIMARY KEY (id),
 
 CONSTRAINT fk_Message_Attachment_Message_id FOREIGN KEY (message_id)
     REFERENCES Message (id) MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION,
     
     CONSTRAINT fk_Message_Attachment_Attachment_id FOREIGN KEY (Attachment_id)
     REFERENCES  Attachment (id)  MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
 OIDS=FALSE
);

CREATE INDEX ix_Message_Attachment
  ON Attachment
  USING btree
  (id);

  CREATE SEQUENCE Message_Attachment_id_seq1
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  ALTER TABLE Message_Attachment
  OWNER TO postgres;

 
 ALTER TABLE MESSAGE ADD Is_attachment boolean;
 ALTER TABLE Attachment add image_Url TEXT;

UPDATE Message SET is_attachment = false WHERE is_attachment is null;





ALTER TABLE Message DROP CONSTRAINT ck_message_message_content_type; 


ALTER TABLE Message add CONSTRAINT ck_message_message_content_type CHECK (message_content_type::text = ANY (ARRAY['LEFTGROUP'::character varying::text, 
 'CREATEGROUP'::character varying::text,
 'ADDTOGROUP'::character varying::text,
 'RENAMEGROUP'::character varying::text,
 'COMMENT'::character varying::text,
 'DELETEGROUP'::character varying::text,
 'TEXT'::character varying::text, 
 'FILE'::character varying::text, 
 'IMAGE'::character varying::text,
 'GITNOTIFICATION'::character varying::text,
 'SNIPPET'::character varying::text,
 'URL'::character varying::text]));



