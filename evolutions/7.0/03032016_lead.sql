#Lead Models


CREATE TABLE company
(
  id bigserial NOT NULL,
  company_name character varying(255),
  address text,
  website character varying(255),
  CONSTRAINT pk_company PRIMARY KEY (id),
  CONSTRAINT uq_company_company_name UNIQUE (company_name)
);
CREATE TABLE company_contacts
(
  id bigserial NOT NULL,
  contact_name character varying(255),
  email_id character varying(255),
  location character varying(255),
  dob timestamp without time zone,
  anniversary_date timestamp without time zone,
  CONSTRAINT pk_company_contacts PRIMARY KEY (id)
);
CREATE TABLE company_contact_info
(
  id bigserial NOT NULL,
  company_id bigint,
  company_contacts_id bigint,
  job_title character varying(255),
  CONSTRAINT pk_company_contact_info PRIMARY KEY (id),
  CONSTRAINT fk_company_contact_info_compa_10 FOREIGN KEY (company_contacts_id)
      REFERENCES company_contacts (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_company_contact_info_compan_9 FOREIGN KEY (company_id)
      REFERENCES company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE client_contact_no
(
  id bigserial NOT NULL,
  company_contacts_id bigint NOT NULL,
  contact_type character varying(6),
  contact_no bigint,
  CONSTRAINT pk_client_contact_no PRIMARY KEY (id),
  CONSTRAINT fk_client_contact_no_company_c_6 FOREIGN KEY (company_contacts_id)
      REFERENCES company_contacts (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_client_contact_no_contact_type CHECK (contact_type::text = ANY (ARRAY['Work'::character varying, 'Mobile'::character varying, 'Home'::character varying]::text[]))
);
CREATE TABLE lead_status
(
  id bigserial NOT NULL,
  status character varying(255),
  description text,
  CONSTRAINT pk_lead_status PRIMARY KEY (id)
);

insert into lead_status (status,description) values ('New',''),('NDA Sent',''),('NDA Signed',''),
('First Discussion',''),('Second Discussion',''),('Proposal Due',''),('Proposal Sent',''),('Negotiating',''),
('Drop',''),('Lost',''),('Won',''),('MSA Sent',''),('MSA Signed',''),('Invoice Raised',''),('Advance Received',''),
('In Progress',''),('Dev Complete',''),('Final Invoice Raised',''),
('Final Payment Received',''),('Closed',''),('No Response','');

CREATE TABLE lead
(
  id bigserial NOT NULL,
  company_id bigint,
  opportunity_title text,
  opportunity_discription text,
  lead_source character varying(17),
  estimated_amount double precision,
  lead_status_id bigint,
  CONSTRAINT pk_lead PRIMARY KEY (id),
  CONSTRAINT fk_lead_company_13 FOREIGN KEY (company_id)
      REFERENCES company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_lead_leadstatus_14 FOREIGN KEY (lead_status_id)
      REFERENCES lead_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ck_lead_lead_source CHECK (lead_source::text = ANY (ARRAY['Cold_Call'::character varying, 'Word_of_Mouth'::character varying, 'Employee'::character varying, 'Self_Generated'::character varying, 'Existing_Customer'::character varying, 'Partner'::character varying, 'Conference'::character varying, 'Other'::character varying]::text[]))
);
CREATE TABLE lead_chat_comment
(
  id bigserial NOT NULL,
  lead_id bigint,
  app_user_id bigint,
  lead_status_id bigint,
  comment text,
  comment_date timestamp without time zone,
  CONSTRAINT pk_lead_chat_comment PRIMARY KEY (id),
  CONSTRAINT fk_lead_chat_comment_leadstat_13 FOREIGN KEY (lead_status_id)
      REFERENCES lead_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE store_file
(
  id bigserial NOT NULL,
  file bytea,
  file_name character varying(255),
  content_type character varying(255),
  CONSTRAINT pk_store_file PRIMARY KEY (id)
);
CREATE TABLE lead_contact_info
(
  id bigserial NOT NULL,
  lead_id bigint,
  company_contact_id bigint,
  job_title character varying(255),
  CONSTRAINT pk_lead_contact_info PRIMARY KEY (id),
  CONSTRAINT fk_lead_contact_info_companyc_17 FOREIGN KEY (company_contact_id)
      REFERENCES company_contacts (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_lead_contact_info_lead_16 FOREIGN KEY (lead_id)
      REFERENCES lead (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


#Third table Many to Many

CREATE TABLE lead_comment
(
  lead_id bigint NOT NULL,
  lead_chat_comment_id bigint NOT NULL,
  CONSTRAINT pk_lead_comment PRIMARY KEY (lead_id, lead_chat_comment_id),
  CONSTRAINT fk_lead_comment_lead_01 FOREIGN KEY (lead_id)
      REFERENCES lead (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_lead_comment_lead_chat_com_02 FOREIGN KEY (lead_chat_comment_id)
      REFERENCES lead_chat_comment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE comment_file
(
  lead_chat_comment_id bigint NOT NULL,
  store_file_id bigint NOT NULL,
  CONSTRAINT pk_comment_file PRIMARY KEY (lead_chat_comment_id, store_file_id),
  CONSTRAINT fk_comment_file_lead_chat_com_01 FOREIGN KEY (lead_chat_comment_id)
      REFERENCES lead_chat_comment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_comment_file_store_file_02 FOREIGN KEY (store_file_id)
      REFERENCES store_file (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE lead_ccinfo
(
  lead_contact_info_id bigint NOT NULL,
  lead_id bigint NOT NULL,
  CONSTRAINT pk_lead_ccinfo PRIMARY KEY (lead_contact_info_id, lead_id),
  CONSTRAINT fk_lead_ccinfo_lead_02 FOREIGN KEY (lead_id)
      REFERENCES lead (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_lead_ccinfo_lead_contact_i_01 FOREIGN KEY (lead_contact_info_id)
      REFERENCES lead_contact_info (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

--ALTER TABLE timesheet RENAME TO time_sheet;
