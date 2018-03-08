CREATE TABLE recruitment_mail_content
(
  id bigserial NOT NULL,
  mail_type character varying(27),
  mail_content text,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_recruitment_mail_content PRIMARY KEY (id),
  CONSTRAINT ck_recruitment_mail_content_mail_type CHECK (mail_type::text = ANY (ARRAY['Interview_Schedule_Email'::character varying, 'Re_Schedule_Email'::character varying, 'Intro_Email'::character varying, 'Schedule_Email'::character varying, 'Interview_Re_Schedule_Email'::character varying]::text[]))
);

DROP TABLE recruitment_selection_round CASCADE;

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
  interview_venue character varying(13),
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
  CONSTRAINT ck_recruitment_selection_round_interview_venue CHECK (interview_venue::text = ANY (ARRAY['Skype'::character varying, 'Telephone'::character varying, 'Thrymr_Office'::character varying]::text[])),
  CONSTRAINT ck_recruitment_selection_round_selection_result CHECK (selection_result::text = ANY (ARRAY['NotSure'::character varying, 'Selected'::character varying, 'Rejected'::character varying]::text[])),
  CONSTRAINT ck_recruitment_selection_round_selection_status CHECK (selection_status::text = ANY (ARRAY['Scheduled'::character varying, 'ReScheduled'::character varying, 'Completed'::character varying, 'Cancelled'::character varying]::text[]))
);