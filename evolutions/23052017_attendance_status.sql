ALTER TABLE public.attendance DROP CONSTRAINT ck_attendance_status;

ALTER TABLE public.attendance
  ADD CONSTRAINT ck_attendance_status CHECK (status::text = ANY (ARRAY['Present'::character varying::text, 'Absent'::character varying::text, 'WFH'::character varying::text, 'CL'::character varying::text]));