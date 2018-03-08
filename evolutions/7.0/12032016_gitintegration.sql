CREATE TABLE git_notification
(
  id bigserial NOT NULL,
  notification_title character varying(255),
  repository character varying(255),
  repository_branch character varying(255),
  no_of_commits integer,
  committed_by character varying(255),
  pusher_email character varying(255),
  origin_json text,
  message_id bigint,
  CONSTRAINT pk_git_notification PRIMARY KEY (id),
  CONSTRAINT fk_git_notification_message_13 FOREIGN KEY (message_id)
      REFERENCES message (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uq_git_notification_message_id UNIQUE (message_id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE git_commit
(
  id bigserial NOT NULL,
  git_notification_id bigint NOT NULL,
  commit_id character varying(255),
  message character varying(255),
  commit_url character varying(255),
  committed_at timestamp without time zone,
  committer_name character varying(255),
  committer_email character varying(255),
  user_name character varying(255),
  CONSTRAINT pk_git_commit PRIMARY KEY (id),
  CONSTRAINT fk_git_commit_git_notificatio_12 FOREIGN KEY (git_notification_id)
      REFERENCES git_notification (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

alter table message drop CONSTRAINT ck_message_message_content_type;
alter table message add CONSTRAINT ck_message_message_content_type CHECK (message_content_type::text = ANY (ARRAY['IMAGE'::character varying, 'GROUPSTATUS'::character varying, 'TEXT'::character varying, 'FILE'::character varying, 'GITNOTIFICATION'::character varying, 'URL'::character varying]::text[]));

