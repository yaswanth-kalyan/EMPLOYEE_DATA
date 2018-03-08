CREATE TABLE task_list
(
  id bigserial NOT NULL,
  task_list_name character varying(255),
  description text,
  created_by_id bigint,
  creation_date timestamp without time zone,
  CONSTRAINT pk_task_list PRIMARY KEY (id),
  CONSTRAINT fk_task_list_createdby_47 FOREIGN KEY (created_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uq_task_list_task_list_name UNIQUE (task_list_name)
)


CREATE TABLE task
(
  id bigserial NOT NULL,
  title character varying(255) NOT NULL,
  description text,
  assign_to_id bigint,
  created_by_id bigint,
  project_id bigint,
  chat_group_id bigint,
  creation_date timestamp without time zone,
  status_id bigint,
  task_list_id bigint,
  CONSTRAINT pk_task PRIMARY KEY (id),
  CONSTRAINT fk_task_assignto_38 FOREIGN KEY (assign_to_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_chatgroup_41 FOREIGN KEY (chat_group_id)
      REFERENCES chat_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_createdby_39 FOREIGN KEY (created_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_project_40 FOREIGN KEY (project_id)
      REFERENCES projects (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_status_42 FOREIGN KEY (status_id)
      REFERENCES task_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_tasklist_43 FOREIGN KEY (task_list_id)
      REFERENCES task_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)

CREATE TABLE task_status
(
  id bigserial NOT NULL,
  status character varying(255),
  description text,
  CONSTRAINT pk_task_status PRIMARY KEY (id)
)



CREATE TABLE tasklist_appusers
(
  app_user_id bigint NOT NULL,
  task_list_id bigint NOT NULL,
  CONSTRAINT pk_tasklist_appusers PRIMARY KEY (app_user_id, task_list_id),
  CONSTRAINT fk_tasklist_appusers_app_user_01 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tasklist_appusers_task_lis_02 FOREIGN KEY (task_list_id)
      REFERENCES task_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)





CREATE TABLE task_comment
(
  id bigserial NOT NULL,
  app_user_id bigint,
  comment text,
  comment_date timestamp without time zone,
  task_id bigint,
  task_status_id bigint,
  CONSTRAINT pk_task_comment PRIMARY KEY (id),
  CONSTRAINT fk_task_comment_appuser_44 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_comment_task_45 FOREIGN KEY (task_id)
      REFERENCES task (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_task_comment_taskstatus_46 FOREIGN KEY (task_status_id)
      REFERENCES task_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)




CREATE TABLE appuser_task_alluser
(
  app_user_id bigint NOT NULL,
  task_id bigint NOT NULL,
  CONSTRAINT pk_appuser_task_alluser PRIMARY KEY (app_user_id, task_id),
  CONSTRAINT fk_appuser_task_alluser_app_u_01 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appuser_task_alluser_task_02 FOREIGN KEY (task_id)
      REFERENCES task (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)



CREATE TABLE appuser_task_adduser
(
  task_id bigint NOT NULL,
  app_user_id bigint NOT NULL,
  CONSTRAINT pk_appuser_task_adduser PRIMARY KEY (task_id, app_user_id),
  CONSTRAINT fk_appuser_task_adduser_app_u_02 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appuser_task_adduser_task_01 FOREIGN KEY (task_id)
      REFERENCES task (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
