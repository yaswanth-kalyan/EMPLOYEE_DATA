CREATE TABLE chat_group
(
  id bigserial NOT NULL,
  description character varying(255),
  name character varying(255),
  group_type character varying(7),
  created_by_id bigint NOT NULL,
  is_disabled boolean,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_chat_group PRIMARY KEY (id),
  CONSTRAINT fk_chat_group_createdby_3 FOREIGN KEY (created_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_chat_group_group_type CHECK (group_type::text = ANY (ARRAY['PUBLIC'::character varying, 'PRIVATE'::character varying]::text[]))
)
WITH (
  OIDS=FALSE
);

-- Index: ix_chat_group_createdby_2

-- DROP INDEX ix_chat_group_createdby_2;

CREATE INDEX ix_chat_group_createdby_2
  ON chat_group
  USING btree
  (created_by_id);


CREATE TABLE message
(
  id bigserial NOT NULL,
  title character varying(255),
  description text,
  message_by_id bigint NOT NULL,
  message_to_id bigint,
  icon character varying(255),
  is_viewd boolean,
  url character varying(255),
  message_content_type character varying(5),
  role character varying(9),
  chat_group_id bigint,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_message PRIMARY KEY (id),
  CONSTRAINT fk_message_chatgroup_7 FOREIGN KEY (chat_group_id)
      REFERENCES chat_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_message_messageby_5 FOREIGN KEY (message_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_message_messageto_6 FOREIGN KEY (message_to_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_message_message_content_type CHECK (message_content_type::text = ANY (ARRAY['IMAGE'::character varying, 'TEXT'::character varying, 'FILE'::character varying]::text[])),
  CONSTRAINT ck_message_role CHECK (role::text = ANY (ARRAY['GROUP'::character varying, 'BB8_ADMIN'::character varying, 'USER'::character varying]::text[]))
)
WITH (
  OIDS=FALSE
);
-- Index: ix_message_chatgroup_7

-- DROP INDEX ix_message_chatgroup_7;

CREATE INDEX ix_message_chatgroup_7
  ON message
  USING btree
  (chat_group_id);

-- Index: ix_message_messageby_5

-- DROP INDEX ix_message_messageby_5;

CREATE INDEX ix_message_messageby_5
  ON message
  USING btree
  (message_by_id);

-- Index: ix_message_messageto_6

-- DROP INDEX ix_message_messageto_6;

CREATE INDEX ix_message_messageto_6
  ON message
  USING btree
  (message_to_id);



CREATE TABLE chat_group_app_user_info
(
  id bigserial NOT NULL,
  chat_group_id bigint NOT NULL,
  app_user_id bigint NOT NULL,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_chat_group_app_user_info PRIMARY KEY (id),
  CONSTRAINT fk_chat_group_app_user_info_ap_4 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_chat_group_app_user_info_ch_3 FOREIGN KEY (chat_group_id)
      REFERENCES chat_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- Index: ix_chat_group_app_user_info_ap_4

-- DROP INDEX ix_chat_group_app_user_info_ap_4;

CREATE INDEX ix_chat_group_app_user_info_ap_4
  ON chat_group_app_user_info
  USING btree
  (app_user_id);

-- Index: ix_chat_group_app_user_info_ch_3

-- DROP INDEX ix_chat_group_app_user_info_ch_3;

CREATE INDEX ix_chat_group_app_user_info_ch_3
  ON chat_group_app_user_info
  USING btree
  (chat_group_id);





CREATE TABLE chat_app_user_last_seen_tab_info
(
  id bigserial NOT NULL,
  logged_in_user_id bigint NOT NULL,
  last_seen_tab bigint NOT NULL,
  last_seen_tab_role character varying(9) NOT NULL,
  previous_last_seen_tab bigint,
  previous_last_seen_tab_role character varying(9),
  last_seen_date timestamp without time zone,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_chat_app_user_last_seen_tab_i PRIMARY KEY (id),
  CONSTRAINT fk_chat_app_user_last_seen_tab_1 FOREIGN KEY (logged_in_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uq_chat_app_user_last_seen_tab_i UNIQUE (logged_in_user_id),
  CONSTRAINT ck_chat_app_user_last_seen_tab_info_last_seen_tab_role CHECK (last_seen_tab_role::text = ANY (ARRAY['GROUP'::character varying, 'BB8_ADMIN'::character varying, 'USER'::character varying]::text[])),
  CONSTRAINT ck_chat_app_user_last_seen_tab_info_previous_last_seen_tab_role CHECK (previous_last_seen_tab_role::text = ANY (ARRAY['GROUP'::character varying, 'BB8_ADMIN'::character varying, 'USER'::character varying]::text[]))
)
WITH (
  OIDS=FALSE
);


-- Index: ix_chat_app_user_last_seen_tab_1

-- DROP INDEX ix_chat_app_user_last_seen_tab_1;

CREATE INDEX ix_chat_app_user_last_seen_tab_1
  ON chat_app_user_last_seen_tab_info
  USING btree
  (logged_in_user_id);








CREATE TABLE notification
(
  id bigserial NOT NULL,
  message_by_id bigint NOT NULL,
  message_to_id bigint,
  to_chat_group_id bigint,
  count bigint,
  is_viewed boolean,
  role character varying(9),
  message_id bigint NOT NULL,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_notification PRIMARY KEY (id),
  CONSTRAINT fk_notification_message_11 FOREIGN KEY (message_id)
      REFERENCES message (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_notification_messageby_8 FOREIGN KEY (message_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_notification_messageto_9 FOREIGN KEY (message_to_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_notification_tochatgroup_10 FOREIGN KEY (to_chat_group_id)
      REFERENCES chat_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_notification_role CHECK (role::text = ANY (ARRAY['GROUP'::character varying, 'BB8_ADMIN'::character varying, 'USER'::character varying]::text[]))
)
WITH (
  OIDS=FALSE
);

-- Index: ix_notification_message_11

-- DROP INDEX ix_notification_message_11;

CREATE INDEX ix_notification_message_11
  ON notification
  USING btree
  (message_id);

-- Index: ix_notification_messageby_8

-- DROP INDEX ix_notification_messageby_8;

CREATE INDEX ix_notification_messageby_8
  ON notification
  USING btree
  (message_by_id);

-- Index: ix_notification_messageto_9

-- DROP INDEX ix_notification_messageto_9;

CREATE INDEX ix_notification_messageto_9
  ON notification
  USING btree
  (message_to_id);

-- Index: ix_notification_tochatgroup_10

-- DROP INDEX ix_notification_tochatgroup_10;

CREATE INDEX ix_notification_tochatgroup_10
  ON notification
  USING btree
  (to_chat_group_id);



