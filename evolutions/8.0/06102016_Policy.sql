
create table incident (
  id                        bigserial not null,
  incident_name             varchar(10),
  description               TEXT,
  image                     bytea,
  image_name                varchar(255),
  image_content_type        varchar(255),
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_incident_incident_name check (incident_name in ('Others','PMO','Sales','Engineer','Finance','HR','Operations','Marketing')),
  constraint pk_incident primary key (id))
;

create table policy (
  id                        bigserial not null,
  policy_name               TEXT,
  file                      bytea,
  file_name                 varchar(255),
  file_content_type         varchar(255),
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_policy primary key (id))
;
