

CREATE TABLE file_comment
(
  id bigserial NOT NULL,
  comment text NOT NULL,
  comment_by_id bigint NOT NULL,
  upload_file_info_id bigint NOT NULL,
  message_id bigint NOT NULL,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_file_comment PRIMARY KEY (id),
  CONSTRAINT fk_comment_by_id FOREIGN KEY (comment_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_file_commentt_message_id FOREIGN KEY (message_id)
      REFERENCES message (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_upload_file_info_id FOREIGN KEY (upload_file_info_id)
      REFERENCES upload_file_info (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT file_comment_message_id_key UNIQUE (message_id)
);


CREATE INDEX ix_file_comment
  ON file_comment
  USING btree
  (id);

CREATE INDEX ix_message_by
  ON file_comment
  USING btree
  (comment_by_id);


CREATE INDEX ix_message_file_comment
  ON file_comment
  USING btree
  (id);

CREATE INDEX ix_upload_file_info_id
  ON file_comment
  USING btree
  (upload_file_info_id);

