create table mailing_list (
  id                        bigserial not null,
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_mailing_list primary key (id))
;