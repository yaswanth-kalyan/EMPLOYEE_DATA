

DROP TABLE chat_app_user_settings ;
DROP TABLE bb8_landing_message;

create table chat_app_user_settings
(
  id bigserial not null,
  logged_in_user_id bigint not null,
  
  is_enable_desktop_notfication boolean  not null,
  left_panel_color text not null,
  created_on timestamp without time zone not null,
  last_update timestamp without time zone not null,

  constraint pk_chat_appuser_settings primary key (id),
  constraint uq_logged_in_user unique (logged_in_user_id),
  constraint fk_chat_appuser_settings_app_user foreign key (logged_in_user_id)
      references app_user (id) match simple
      on update no action on delete no action
);

create table bb8_landing_message
(
  id bigserial not null,
  description text not null,
  added_by_id bigint not null,
  created_on timestamp without time zone not null,
  last_update timestamp without time zone not null,
  constraint pk_bb8_message primary key (id),
  constraint fk_bb8_message_app_user foreign key (added_by_id)
      references app_user (id) match simple
      on update no action on delete no action
);

alter table chat_group alter column name set not null;
alter table chat_group add constraint uq_chat_group unique (name);
