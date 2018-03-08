ALTER TABLE client_contact_no ADD COLUMN country_code character varying(255);

CREATE TABLE notification_alert
(
  id bigserial NOT NULL,
  notification character varying(255),
  alert boolean,
  lead_id bigint,
  app_user_id bigint,
  notification_date timestamp without time zone,
  CONSTRAINT pk_notification_alert PRIMARY KEY (id)
)


