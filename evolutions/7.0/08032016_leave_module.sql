CREATE TABLE working_days
(
  id bigserial NOT NULL,
  day character varying(9),
  days_section character varying(11),
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_working_days PRIMARY KEY (id),
  CONSTRAINT ck_working_days_day CHECK (day::text = ANY (ARRAY['WEDNESDAY'::character varying, 'MONDAY'::character varying, 'THURSDAY'::character varying, 'SUNDAY'::character varying, 'TUESDAY'::character varying, 'FRIDAY'::character varying, 'SATURDAY'::character varying]::text[])),
  CONSTRAINT ck_working_days_days_section CHECK (days_section::text = ANY (ARRAY['HALF_DAY'::character varying, 'FULL_DAY'::character varying, 'NOT_WORKING'::character varying]::text[]))
);

CREATE TABLE leave_type
(
  id bigserial NOT NULL,
  leave_type character varying(255),
  carry_forward boolean,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_leave_type PRIMARY KEY (id)
);
CREATE TABLE entitlement
(
  id bigserial NOT NULL,
  leave_type_id bigint,
  leave_period timestamp without time zone,
  no_of_days double precision,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_entitlement PRIMARY KEY (id),
  CONSTRAINT fk_entitlement_leavetype_12 FOREIGN KEY (leave_type_id)
      REFERENCES leave_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE entitlement_app_user
(
  entitlement_id bigint NOT NULL,
  app_user_id bigint NOT NULL,
  CONSTRAINT pk_entitlement_app_user PRIMARY KEY (entitlement_id, app_user_id),
  CONSTRAINT fk_entitlement_app_user_app_u_02 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_entitlement_app_user_entit_01 FOREIGN KEY (entitlement_id)
      REFERENCES entitlement (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE holidays
(
  id bigserial NOT NULL,
  year timestamp without time zone,
  holiday_for character varying(255),
  holiday_date timestamp without time zone,
  compensatory boolean,
  corresponding_working_day timestamp without time zone,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_holidays PRIMARY KEY (id)
);

CREATE TABLE applied_leaves
(
  id bigserial NOT NULL,
  leave_status character varying(16),
  leave_type_id bigint,
  start_date timestamp without time zone,
  end_date timestamp without time zone,
  total_leaves double precision,
  reason text,
  app_user_id bigint,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_applied_leaves PRIMARY KEY (id),
  CONSTRAINT fk_applied_leaves_appuser_2 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_applied_leaves_leavetype_1 FOREIGN KEY (leave_type_id)
      REFERENCES leave_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_applied_leaves_leave_status CHECK (leave_status::text = ANY (ARRAY['CANCELLED'::character varying, 'NOT_APPLIED'::character varying, 'APLLIED'::character varying, 'PENDING_APPROVAL'::character varying, 'TAKEN'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying]::text[]))
);

CREATE TABLE date_wise_applied_leaves
(
  id bigserial NOT NULL,
  applied_leaves_id bigint NOT NULL,
  leave_date timestamp without time zone,
  du_enum character varying(8),
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_date_wise_applied_leaves PRIMARY KEY (id),
  CONSTRAINT fk_date_wise_applied_leaves_a_11 FOREIGN KEY (applied_leaves_id)
      REFERENCES applied_leaves (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_date_wise_applied_leaves_du_enum CHECK (du_enum::text = ANY (ARRAY['HALF_DAY'::character varying, 'FULL_DAY'::character varying]::text[]))
);


CREATE TABLE leaves
(
  id bigserial NOT NULL,
  added_leaves double precision,
  used_leaves double precision,
  remaining_leaves double precision,
  leave_type_id bigint,
  leave_status character varying(16),
  app_user_id bigint,
  applied_leaves_id bigint,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_leaves PRIMARY KEY (id),
  CONSTRAINT fk_leaves_appliedleaves_17 FOREIGN KEY (applied_leaves_id)
      REFERENCES applied_leaves (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_leaves_appuser_16 FOREIGN KEY (app_user_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_leaves_leavetype_15 FOREIGN KEY (leave_type_id)
      REFERENCES leave_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_leaves_leave_status CHECK (leave_status::text = ANY (ARRAY['CANCELLED'::character varying, 'NOT_APPLIED'::character varying, 'APLLIED'::character varying, 'PENDING_APPROVAL'::character varying, 'TAKEN'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying]::text[]))
);


alter table leaves add column  year timestamp without time zone;
alter table applied_leaves add column  approved_by_id bigint;