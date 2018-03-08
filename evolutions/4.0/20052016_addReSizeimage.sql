alter TABLE upload_file_info add column re_Size_Image bytea ;
alter TABLE upload_file_info add column file_Size text ;



ALTER TABLE upload_file_info ALTER COLUMN upload_image SET NOT NULL;
ALTER TABLE upload_file_info ALTER COLUMN upload_file_name SET NOT NULL;



CREATE TABLE file_like
(
  id bigserial NOT NULL,
  like_By_id bigint NOT NULL,
  upload_file_info_id bigint NOT NULL,
  created_on timestamp without time zone NOT NULL,
  CONSTRAINT pk_file_like PRIMARY KEY (id),
  CONSTRAINT fk_like_By_id FOREIGN KEY (like_By_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_upload_file_info_id FOREIGN KEY (upload_file_info_id)
      REFERENCES upload_file_info (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE INDEX ix_file_like
  ON file_like
  USING btree
  (id);

CREATE INDEX ix_like_By_
  ON file_like
  USING btree
  (like_By_id);


CREATE INDEX ix_file_like_upload_file_info_id
  ON file_like
  USING btree
  (upload_file_info_id);
