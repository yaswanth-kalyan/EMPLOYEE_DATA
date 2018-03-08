
alter table app_user add column essl_id bigint;
alter table app_user add CONSTRAINT uq_app_user_essl_id UNIQUE (essl_id);

alter table attendance add column essl_intime timestamp without time zone, add column essl_outtime timestamp without time zone,
 add column essl_spendtime character varying(255),
 add column  time_in_office character varying(255),
 add column  essl_break_time character varying(255);





CREATE TABLE biometric_attendance
(
  id bigserial NOT NULL,
  essl_id bigint,
  date timestamp without time zone,
  status_code integer,
  CONSTRAINT pk_biometric_attendance PRIMARY KEY (id)
)

