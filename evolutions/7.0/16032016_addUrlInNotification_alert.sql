Alter table notification_alert add column url character varying(255)
Alter table notification_alert add column notified_by_id bigint;
Alter table notification_alert add column notified_to_id bigint;
Alter table notification_alert add column role_id bigint;