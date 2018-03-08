
//view for Daily wise status report

CREATE OR REPLACE VIEW filled_report AS 
 SELECT au.id
   FROM daily_report dr
     JOIN users_daily_report udr ON dr.id = udr.daily_report_id
     RIGHT JOIN app_user au ON dr.app_user_id = au.id
  WHERE to_char(udr.date, 'dd/mm/yyyy'::text) = to_char(now(), 'dd/mm/yyyy'::text) AND au.status::text = 'Active'::text
  GROUP BY au.id;
  
  //View for Company weekly Attendance Report.(For Absenties as per Manual Attendance)
  
CREATE OR REPLACE VIEW absent AS 
SELECT ad.date,
   count(ad.date) AS absent
  FROM attendance ad
    JOIN app_user a ON ad.app_user_id = a.id
 WHERE ad.status::text = 'Absent'::text
 GROUP BY ad.date
 ORDER BY ad.date;
  
  //View for Company weekly Attendance Report.(For ESSL Attendance)
  
  CREATE OR REPLACE VIEW ess_total AS 
SELECT count(ad.date) AS essl_present,
   ad.date AS essl_date,
   sum(split_part(ad.time_in_office::text, ':'::text, 1)::integer) * 60 AS timehh_in_office,
   sum(split_part(ad.time_in_office::text, ':'::text, 2)::integer) AS timemin_in_office,
   sum(to_char(ad.essl_intime, 'HH24'::text)::integer) * 60 AS essl_in_hh,
   sum(to_char(ad.essl_intime, 'MI'::text)::integer) AS essl_in_min,
   sum(to_char(ad.essl_outtime, 'HH24'::text)::integer) * 60 AS essl_out_hh,
   sum(to_char(ad.essl_outtime, 'MI'::text)::integer) AS essl_out_min,
   sum(split_part(ad.essl_spendtime::text, ':'::text, 1)::integer) * 60 AS essl_spend_hh,
   sum(split_part(ad.essl_spendtime::text, ':'::text, 2)::integer) AS essl_spend_min,
   sum(split_part(ad.essl_break_time::text, ':'::text, 1)::integer) * 60 AS essl_break_hh,
   sum(split_part(ad.essl_break_time::text, ':'::text, 2)::integer) AS essl_break_min
  FROM attendance ad
    JOIN app_user a ON ad.app_user_id = a.id
 WHERE ad.status::text = 'Present'::text OR ad.status IS NULL
 GROUP BY ad.date
 ORDER BY ad.date;
  
  //View for Company weekly Attendance Report.(For Manual Attendance)
  
  
  CREATE OR REPLACE VIEW total AS 
SELECT ( SELECT count(a_1.status) AS total
          FROM app_user a_1
         WHERE a_1.status::text = 'Active'::text) AS total,
   count(ad.date) AS present,
   ad.date,
   sum(to_char(ad.in_time, 'HH24'::text)::integer) * 60 AS in_hh,
   sum(to_char(ad.in_time, 'MI'::text)::integer) AS in_min,
   sum(to_char(ad.out_time, 'MI'::text)::integer) AS out_min,
   sum(to_char(ad.out_time, 'HH24'::text)::integer) * 60 AS out_hh,
   sum(split_part(ad.spend_time::text, ':'::text, 1)::integer) * 60 AS spend_hh,
   sum(split_part(ad.spend_time::text, ':'::text, 2)::integer) AS spend_min
  FROM attendance ad
    JOIN app_user a ON ad.app_user_id = a.id
 WHERE ad.status::text = 'Present'::text
 GROUP BY ad.date
 ORDER BY ad.date;
 
   //View for Company weekly Attendance Report.(For WFH(Work from home) people)
 
CREATE OR REPLACE VIEW wfh AS 
SELECT ad.date,
  count(ad.date) AS wfh
 FROM attendance ad
   JOIN app_user a ON ad.app_user_id = a.id
WHERE ad.status::text = 'WFH'::text
GROUP BY ad.date
ORDER BY ad.date;


 //views for Leave History & Daily Status History Reports
 
 -- View: leave_history

-- DROP VIEW leave_history;

CREATE OR REPLACE VIEW leave_history AS 
 SELECT a.id,
    to_char(dwal.leave_date, 'dd'::text)::bigint AS leave_day,
    to_char(dwal.leave_date, 'yyyy'::text) AS leave_year,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 1 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS jan_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 1 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS jan_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 2 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS feb_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 2 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS feb_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 3 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS mar_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 3 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS mar_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 4 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS apr_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 4 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS apr_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 5 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS may_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 5 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS may_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 6 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS jun_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 6 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS jun_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 7 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS jul_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 7 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS jul_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 8 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS aug_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 8 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS aug_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 9 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS sep_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 9 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS sep_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 10 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS oct_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 10 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS oct_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 11 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS nov_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 11 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS nov_diff,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 12 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS dec_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 12 THEN date_part('epoch'::text, dwal.leave_date - dwal.created_on)
            ELSE NULL::double precision
        END) AS dec_diff
   FROM date_wise_applied_leaves dwal
     LEFT JOIN applied_leaves al ON dwal.applied_leaves_id = al.id
     LEFT JOIN app_user a ON al.app_user_id = a.id
  WHERE a.status::text = 'Active'::text AND (al.leave_status::text = ANY (ARRAY['PENDING_APPROVAL'::character varying::text, 'APPROVED'::character varying::text]))
  GROUP BY a.id, (to_char(dwal.leave_date, 'dd'::text)::bigint), (to_char(dwal.leave_date, 'yyyy'::text))
  ORDER BY a.id, (to_char(dwal.leave_date, 'dd'::text)::bigint);


 
-- View: holiday_month

-- DROP VIEW holiday_month;

CREATE OR REPLACE VIEW public.holiday_month AS 
 SELECT to_char(holidays.holiday_date, 'dd'::text)::integer AS holiday,
    to_char(holidays.holiday_date, 'yyyy'::text) AS holiday_year,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 1 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS jan_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 2 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS feb_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 3 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS mar_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 4 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS apr_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 5 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS may_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 6 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS jun_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 7 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS jul_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 8 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS aug_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 9 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS sep_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 10 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS oct_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 11 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS nov_holiday,
    max(
        CASE
            WHEN to_char(holidays.holiday_date, 'mm'::text)::bigint = 12 THEN date(holidays.holiday_date)
            ELSE NULL::date
        END) AS dec_holiday
   FROM holidays
  GROUP BY (to_char(holidays.holiday_date, 'dd'::text)::integer), (to_char(holidays.holiday_date, 'yyyy'::text)), (date(now()))
  ORDER BY (to_char(holidays.holiday_date, 'dd'::text)::integer);


-- View: corresponding_working_day

-- DROP VIEW corresponding_working_day;

CREATE OR REPLACE VIEW public.corresponding_working_day AS 
 SELECT to_char(holidays.corresponding_working_day, 'dd'::text)::integer AS corresponding_working_day,
    to_char(holidays.corresponding_working_day, 'yyyy'::text) AS corresponding_working_day_year,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 1 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS jan_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 2 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS feb_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 3 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS mar_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 4 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS apr_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 5 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS may_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 6 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS jun_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 7 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS jul_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 8 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS aug_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 9 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS sep_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 10 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS oct_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 11 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS nov_corresponding_working_day,
    max(
        CASE
            WHEN to_char(holidays.corresponding_working_day, 'mm'::text)::bigint = 12 THEN date(holidays.corresponding_working_day)
            ELSE NULL::date
        END) AS dec_corresponding_working_day
   FROM holidays
  WHERE holidays.corresponding_working_day IS NOT NULL
  GROUP BY (to_char(holidays.corresponding_working_day, 'dd'::text)::integer), (to_char(holidays.corresponding_working_day, 'yyyy'::text))
  ORDER BY (to_char(holidays.corresponding_working_day, 'dd'::text)::integer);




-- View: daily_status_history

-- DROP VIEW daily_status_history;

CREATE OR REPLACE VIEW public.daily_status_history AS 
 SELECT to_char(udr.date, 'dd'::text)::integer AS d_day,
    dr.app_user_id,
    to_char(udr.date, 'yyyy'::text) AS daily_status_year,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 1 THEN date(udr.date)
            ELSE NULL::date
        END) AS jan_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 2 THEN date(udr.date)
            ELSE NULL::date
        END) AS feb_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 3 THEN date(udr.date)
            ELSE NULL::date
        END) AS mar_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 4 THEN date(udr.date)
            ELSE NULL::date
        END) AS apr_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 5 THEN date(udr.date)
            ELSE NULL::date
        END) AS may_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 6 THEN date(udr.date)
            ELSE NULL::date
        END) AS jun_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 7 THEN date(udr.date)
            ELSE NULL::date
        END) AS jul_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 8 THEN date(udr.date)
            ELSE NULL::date
        END) AS aug_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 9 THEN date(udr.date)
            ELSE NULL::date
        END) AS sep_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 10 THEN date(udr.date)
            ELSE NULL::date
        END) AS oct_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 11 THEN date(udr.date)
            ELSE NULL::date
        END) AS nov_d_day,
    max(
        CASE
            WHEN to_char(udr.date, 'mm'::text)::bigint = 12 THEN date(udr.date)
            ELSE NULL::date
        END) AS dec_d_day
   FROM users_daily_report udr
     LEFT JOIN daily_report dr ON udr.daily_report_id = dr.id
  GROUP BY dr.app_user_id, (to_char(udr.date, 'dd'::text)::integer), (to_char(udr.date, 'yyyy'::text))
  ORDER BY dr.app_user_id, (to_char(udr.date, 'dd'::text)::integer);

