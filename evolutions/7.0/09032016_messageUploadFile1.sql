

CREATE TABLE upload_file_info
(
 id bigserial NOT NULL,
 upload_image bytea,
 upload_file_content_type character varying(255),
 upload_file_name character varying(255),
 app_user_id bigint NOT NULL,
 created_on timestamp without time zone NOT NULL,
 last_update timestamp without time zone NOT NULL,
 CONSTRAINT pk_upload_file_info PRIMARY KEY (id),
 CONSTRAINT fk_upload_file_info_appuser_29 FOREIGN KEY (app_user_id)
     REFERENCES app_user (id) MATCH SIMPLE
     ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
 OIDS=FALSE
);


CREATE INDEX ix_upload_file_info_appuser_26
  ON upload_file_info
  USING btree
  (appuser_id);

alter table upload_File_info add column message_id bigint;
alter table upload_File_info add
      CONSTRAINT fk_message FOREIGN KEY (message_id)
      REFERENCES message (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

      alter table Message DROP COLUMN upload_image;
      alter table message drop column  upload_file_content_type;


