CREATE TABLE public.pe_question
(
  id bigint NOT NULL DEFAULT nextval('pe_question_id_seq'::regclass),
  question text,
  note text,
  weightage double precision,
  appraisal_type character varying(18),
  question_status character varying(9),
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_pe_question PRIMARY KEY (id),
  CONSTRAINT ck_pe_question_appraisal_type CHECK (appraisal_type::text = ANY (ARRAY['Employee_Appraisal'::character varying, 'Self_Appraisal'::character varying]::text[])),
  CONSTRAINT ck_pe_question_question_status CHECK (question_status::text = ANY (ARRAY['Active'::character varying, 'Completed'::character varying, 'Inactive'::character varying]::text[]))
);
CREATE TABLE public.pe_self_appraisal
(
  id bigint NOT NULL DEFAULT nextval('pe_self_appraisal_id_seq'::regclass),
  app_user_id bigint,
  month_date timestamp without time zone,
  saar double precision,
  war double precision,
  issue text,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_pe_self_appraisal PRIMARY KEY (id),
  CONSTRAINT fk_pe_self_appraisal_appuser_53 FOREIGN KEY (app_user_id)
      REFERENCES public.app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE public.pe_self_appraisal_answer
(
  id bigint NOT NULL DEFAULT nextval('pe_self_appraisal_answer_id_seq'::regclass),
  pe_self_appraisal_id bigint NOT NULL,
  performance_question_id bigint,
  rate bigint,
  answer text,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_pe_self_appraisal_answer PRIMARY KEY (id),
  CONSTRAINT fk_pe_self_appraisal_answer_p_54 FOREIGN KEY (pe_self_appraisal_id)
      REFERENCES public.pe_self_appraisal (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_pe_self_appraisal_answer_p_55 FOREIGN KEY (performance_question_id)
      REFERENCES public.pe_question (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE public.pe_employee_appraisal
(
  id bigint NOT NULL DEFAULT nextval('pe_employee_appraisal_id_seq'::regclass),
  project_manager_id bigint,
  project_team_member_id bigint,
  month_date timestamp without time zone,
  pr double precision,
  war double precision,
  issue text,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_pe_employee_appraisal PRIMARY KEY (id),
  CONSTRAINT fk_pe_employee_appraisal_proj_49 FOREIGN KEY (project_manager_id)
      REFERENCES public.app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_pe_employee_appraisal_proj_50 FOREIGN KEY (project_team_member_id)
      REFERENCES public.app_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE public.pe_employee_appraisal_answer
(
  id bigint NOT NULL DEFAULT nextval('pe_employee_appraisal_answer_id_seq'::regclass),
  pe_employee_appraisal_id bigint NOT NULL,
  performance_question_id bigint,
  rate bigint,
  answer text,
  created_on timestamp without time zone NOT NULL,
  last_update timestamp without time zone NOT NULL,
  CONSTRAINT pk_pe_employee_appraisal_answer PRIMARY KEY (id),
  CONSTRAINT fk_pe_employee_appraisal_answ_51 FOREIGN KEY (pe_employee_appraisal_id)
      REFERENCES public.pe_employee_appraisal (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_pe_employee_appraisal_answ_52 FOREIGN KEY (performance_question_id)
      REFERENCES public.pe_question (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);