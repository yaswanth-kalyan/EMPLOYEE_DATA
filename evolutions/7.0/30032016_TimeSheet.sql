CREATE TABLE timesheet
(
  id bigserial NOT NULL,
  app_user_id bigint,
  project_id bigint,
  hours double precision,
  date timestamp without time zone,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_timesheet PRIMARY KEY (id),
  CONSTRAINT fk_timesheet_appuser_44 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_timesheet_project_45 FOREIGN KEY (project_id)
      REFERENCES projects (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE TABLE timesheet_user_remark
(
  id bigserial NOT NULL,
  app_user_id bigint,
  remark text,
  date timestamp without time zone,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_timesheet_user_remark PRIMARY KEY (id),
  CONSTRAINT fk_timesheet_user_remark_appu_46 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

Alter table app_user add column thumbnail bytea;

alter table lead drop  CONSTRAINT ck_lead_lead_source; 
alter table lead add  CONSTRAINT ck_lead_lead_source CHECK (lead_source::text = ANY (ARRAY['Cold_Call'::character varying, 'Word_of_Mouth'::character varying, 'Employee'::character varying, 'Self_Generated'::character varying, 'Existing_Customer'::character varying, 'Reference'::character varying, 'Partner'::character varying, 'Conference'::character varying, 'Whatsapp_Groups'::character varying, 'Other'::character varying]::text[]));

UPDATE notification_alert SET url = REPLACE(url, 'leadManagement', 'lead-management')--04042016
--drop table orders cascade;