ALTER TABLE public.applied_leaves ADD COLUMN rejected_reason text;

ALTER TABLE public.entitlement ADD COLUMN worked_date character varying(255);