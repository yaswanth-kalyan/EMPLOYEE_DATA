/*Alter table pe_question add column pe_question_type character varying(33) default 'Organizational_Core_Competencies';
alter table pe_question add CONSTRAINT ck_pe_question_pe_question_type CHECK (pe_question_type::text = ANY (ARRAY['Organizational_Core_Competencies'::character varying, 'Job_Family_Competencies'::character varying, 'Key_Job_Responsibilities'::character varying, 'Goals_And_Projects'::character varying]::text[]));
*/

ALTER TABLE applied_leaves ADD year timestamp without time zone default '2016-01-01 00:00:00';

create table deduct_leave (
  id                        bigserial not null,
  applied_leaves_id         bigint not null,
  leave_type_id             bigint,
  deduct_leaves             float,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_deduct_leave primary key (id))
;

ALTER TABLE date_wise_applied_leaves ADD apply_user_id bigint;