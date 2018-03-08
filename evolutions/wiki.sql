create table bug (
  id                        bigserial not null,
  title                     varchar(255) not null,
  description               text,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_bug primary key (id))
;

create table epic (
  id                        bigserial not null,
  name                      varchar(255) not null,
  description               varchar(255),
  road_map_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_epic primary key (id))
;

create table ptask (
  id                        bigserial not null,
  name                      varchar(255),
  description               text,
  estimated_time            Decimal(10,2),
  actual_time               Decimal(10,2),
  planned_start_date        timestamp,
  planned_end_date          timestamp,
  actual_start_date         timestamp,
  actual_end_date           timestamp,
  user_story_id             bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_ptask primary key (id))
;

create table page (
  id                        bigserial not null,
  title                     varchar(255) not null,
  is_active                 boolean,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_page primary key (id))
;

create table page_history (
  id                        bigserial not null,
  version                   integer,
  content                   text,
  page_id                   bigint,
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_page_history primary key (id))
;

create table road_map (
  id                        bigserial not null,
  title                     varchar(255) not null,
  description               text,
  project_id                bigint not null,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_road_map primary key (id))
;

create table sprint (
  id                        bigserial not null,
  name                      varchar(255),
  start_date                timestamp,
  end_date                  timestamp,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_sprint primary key (id))
;

create table user_story (
  id                        bigserial not null,
  road_map_id               bigint not null,
  name                      varchar(255) not null,
  description               varchar(255),
  epic_id                   bigint not null,
  sprint_id                 bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_user_story primary key (id))
;
