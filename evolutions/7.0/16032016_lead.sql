CREATE TABLE lead_summary
(
  id bigserial NOT NULL,
  lead_status_id bigint,
  total_status integer,
  total_estimated_amount double precision,
  CONSTRAINT pk_lead_summary PRIMARY KEY (id),
  CONSTRAINT fk_lead_summary_leadstatus_21 FOREIGN KEY (lead_status_id)
      REFERENCES lead_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)