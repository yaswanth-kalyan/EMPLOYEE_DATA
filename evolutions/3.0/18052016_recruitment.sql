DROP TABLE job_category, job_role,job,skill,source,job_mandatoryskills,job_desiredskills,applicant;


CREATE TABLE recruitment_category
(
  id bigserial NOT NULL,
  job_category_name character varying(255),
  description text,
  CONSTRAINT pk_recruitment_category PRIMARY KEY (id)
)

CREATE TABLE recruitment_role
(
  id bigserial NOT NULL,
  job_role_name character varying(255),
  description text,
  CONSTRAINT pk_recruitment_role PRIMARY KEY (id)
)


CREATE TABLE recruitment_job
(
  id bigserial NOT NULL,
  job_id character varying(255),
  job_description bytea,
  file_name character varying(255),
  file_content_type character varying(255),
  recruitment_category_id bigint,
  recruitment_role_id bigint,
  no_of_openning integer,
  job_location character varying(9),
  job_type character varying(9),
  open_date timestamp without time zone,
  last_date timestamp without time zone,
  job_status character varying(7),
  remark text,
  created_by_id bigint,
  CONSTRAINT pk_recruitment_job PRIMARY KEY (id),
  CONSTRAINT fk_recruitment_job_createdby_55 FOREIGN KEY (created_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_job_recruitmen_53 FOREIGN KEY (recruitment_category_id)
      REFERENCES recruitment_category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_job_recruitmen_54 FOREIGN KEY (recruitment_role_id)
      REFERENCES recruitment_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_recruitment_job_job_location CHECK (job_location::text = ANY (ARRAY['Hyderabad'::character varying, 'Bangalore'::character varying]::text[])),
  CONSTRAINT ck_recruitment_job_job_status CHECK (job_status::text = ANY (ARRAY['Closed'::character varying, 'Open'::character varying, 'Defered'::character varying]::text[])),
  CONSTRAINT ck_recruitment_job_job_type CHECK (job_type::text = ANY (ARRAY['Full-Time'::character varying, 'Part-Time'::character varying, 'Contract'::character varying]::text[]))
)

CREATE TABLE recruitment_skill
(
  id bigserial NOT NULL,
  skill_name character varying(255),
  description text,
  CONSTRAINT pk_recruitment_skill PRIMARY KEY (id)
)



CREATE TABLE recruitment_source
(
  id bigserial NOT NULL,
  source_name character varying(255),
  description text,
  CONSTRAINT pk_recruitment_source PRIMARY KEY (id)
)
CREATE TABLE recruitment_mandatoryskills
(
  recruitment_job_id bigint NOT NULL,
  recruitment_skill_id bigint NOT NULL,
  CONSTRAINT pk_recruitment_mandatoryskills PRIMARY KEY (recruitment_job_id, recruitment_skill_id),
  CONSTRAINT fk_recruitment_mandatoryskill_01 FOREIGN KEY (recruitment_job_id)
      REFERENCES recruitment_job (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_mandatoryskill_02 FOREIGN KEY (recruitment_skill_id)
      REFERENCES recruitment_skill (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)

CREATE TABLE recruitment_desiredskills
(
  recruitment_job_id bigint NOT NULL,
  recruitment_skill_id bigint NOT NULL,
  CONSTRAINT pk_recruitment_desiredskills PRIMARY KEY (recruitment_job_id, recruitment_skill_id),
  CONSTRAINT fk_recruitment_desiredskills__01 FOREIGN KEY (recruitment_job_id)
      REFERENCES recruitment_job (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_desiredskills__02 FOREIGN KEY (recruitment_skill_id)
      REFERENCES recruitment_skill (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)


CREATE TABLE recruitment_applicant
(
  id bigserial NOT NULL,
  application_id character varying(255),
  applicant_name character varying(255),
  contact_no bigint,
  email_id character varying(255),
  apply_date timestamp without time zone,
  dob timestamp without time zone,
  refered_by_id bigint,
  applicant_category_id bigint,
  recruitment_role_id bigint,
  prefered_location character varying(9),
  exprience double precision,
  recruitment_job_id bigint,
  resume bytea,
  file_name character varying(255),
  file_content_type character varying(255),
  recruitment_source_id bigint,
  status character varying(16),
  CONSTRAINT pk_recruitment_applicant PRIMARY KEY (id),
  CONSTRAINT fk_recruitment_applicant_appl_49 FOREIGN KEY (applicant_category_id)
      REFERENCES recruitment_category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_applicant_recr_50 FOREIGN KEY (recruitment_role_id)
      REFERENCES recruitment_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_applicant_recr_51 FOREIGN KEY (recruitment_job_id)
      REFERENCES recruitment_job (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_applicant_recr_52 FOREIGN KEY (recruitment_source_id)
      REFERENCES recruitment_source (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_applicant_refe_48 FOREIGN KEY (refered_by_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_recruitment_applicant_prefered_location CHECK (prefered_location::text = ANY (ARRAY['Hyderabad'::character varying, 'Bangalore'::character varying]::text[])),
  CONSTRAINT ck_recruitment_applicant_status CHECK (status::text = ANY (ARRAY['Shortlisted'::character varying, 'Selected'::character varying, 'Rejected'::character varying, 'Offered'::character varying, 'Joined'::character varying, 'Registered'::character varying, 'Abandoned'::character varying, 'Offered_Accepted'::character varying, 'NotJoined'::character varying]::text[]))
)
