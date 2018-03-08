Alter table lead add column last_update timestamp without time zone;

UPDATE lead SET last_update = '2016-03-22 12:29:03.316'

DROP TABLE lead_summary


ALTER TABLE company_contacts ADD CONSTRAINT unique_contact_name_1 UNIQUE (contact_name); --23-03-2016

