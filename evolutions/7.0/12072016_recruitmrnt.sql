CREATE TABLE interviewer_app_user
(
  id bigserial NOT NULL,
  interviewer_id bigint,
  CONSTRAINT pk_interviewer_app_user PRIMARY KEY (id),
  CONSTRAINT fk_interviewer_app_user_inter_27 FOREIGN KEY (interviewer_id)
      REFERENCES app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE recruitment_selection_round
(
  id bigserial NOT NULL,
  recruitment_interview_type_id bigint,
  question_template_id bigint,
  conduct_date timestamp without time zone,
  feedback bytea,
  recruitment_applicant_id bigint,
  time_reschedule integer,
  remark character varying(255),
  selection_status character varying(11),
  selection_result character varying(8),
  CONSTRAINT pk_recruitment_selection_round PRIMARY KEY (id),
  CONSTRAINT fk_recruitment_selection_roun_62 FOREIGN KEY (recruitment_interview_type_id)
      REFERENCES recruitment_interview_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_selection_roun_63 FOREIGN KEY (question_template_id)
      REFERENCES recruitment_question_template (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_selection_roun_64 FOREIGN KEY (recruitment_applicant_id)
      REFERENCES recruitment_applicant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_recruitment_selection_round_selection_result CHECK (selection_result::text = ANY (ARRAY['Selected'::character varying, 'Rejected'::character varying]::text[])),
  CONSTRAINT ck_recruitment_selection_round_selection_status CHECK (selection_status::text = ANY (ARRAY['Scheduled'::character varying, 'ReScheduled'::character varying, 'Completed'::character varying, 'Cancelled'::character varying]::text[]))
);

CREATE TABLE recruitment_selection_round_interviewer
(
  interviewer_app_user_id bigint NOT NULL,
  recruitment_selection_round_id bigint NOT NULL,
  CONSTRAINT pk_recruitment_selection_round_interviewer PRIMARY KEY (interviewer_app_user_id, recruitment_selection_round_id),
  CONSTRAINT fk_recruitment_selection_roun_01 FOREIGN KEY (interviewer_app_user_id)
      REFERENCES interviewer_app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_selection_roun_02 FOREIGN KEY (recruitment_selection_round_id)
      REFERENCES recruitment_selection_round (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE recruitment_interviewer_feedback
(
  id bigserial NOT NULL,
  recruitment_applicant_id bigint,
  recruitment_selection_round_id bigint,
  feed_back character varying(255),
  remark text,
  interviewer_app_user_id bigint,
  CONSTRAINT pk_recruitment_interviewer_feedb PRIMARY KEY (id),
  CONSTRAINT fk_recruitment_interviewer_fe_56 FOREIGN KEY (recruitment_applicant_id)
      REFERENCES recruitment_applicant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_interviewer_fe_57 FOREIGN KEY (recruitment_selection_round_id)
      REFERENCES recruitment_selection_round (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recruitment_interviewer_fe_58 FOREIGN KEY (interviewer_app_user_id)
      REFERENCES interviewer_app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE recruitment_interview_type
(
  id bigserial NOT NULL,
  interview_type_name character varying(255),
  description text,
  CONSTRAINT pk_recruitment_interview_type PRIMARY KEY (id)
);

CREATE TABLE recruitment_question_template
(
  id bigserial NOT NULL,
  question_template_name character varying(255),
  question_template bytea,
  description text,
  filename character varying(255),
  file_content_type character varying(255),
  CONSTRAINT pk_recruitment_question_template PRIMARY KEY (id)
);
