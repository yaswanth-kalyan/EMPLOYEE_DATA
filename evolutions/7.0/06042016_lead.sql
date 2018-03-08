Alter table lead add column app_user_id bigint;

UPDATE lead SET app_user_id =1;--who are ctreated this lead that appUserId use

Alter table lead add column created_on timestamp without time zone;

UPDATE lead SET created_on = '2016-03-22 12:29:03.316';