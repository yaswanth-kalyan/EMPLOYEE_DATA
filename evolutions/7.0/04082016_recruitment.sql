Alter table recruitment_job add column job_experience double precision DEFAULT 0;

create table recruitment_reference (
  id                        bigserial not null,
  candidate_name            varchar(255),
  candidate_email           varchar(255),
  experience                float,
  resume                    bytea,
  resume_name               varchar(255),
  resume_content_tyep       varchar(255),
  refered_by_id             bigint,
  recruitment_job_id        bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint uq_recruitment_reference_candida unique (candidate_email),
  constraint pk_recruitment_reference primary key (id))
;