-- View: public.biometric_attendance_history

DROP VIEW public.biometric_attendance_history;

CREATE OR REPLACE VIEW biometric_attendance_history AS 
 SELECT a.id,
    a.full_name,
    a.employee_id,
    to_char(ad.date, 'dd'::text) AS attendance_date,
    to_char(ad.date, 'yyyy'::text) AS attendance_year,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 1 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS jan_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 2 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS feb_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 3 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS mar_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 4 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS apr_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 5 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS may_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 6 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS jun_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 7 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS jul_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 8 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS aug_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 9 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS sep_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 10 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS oct_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 11 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS nov_essl_spendtime,
    max(
        CASE
            WHEN to_char(ad.date, 'mm'::text)::bigint = 12 THEN round(split_part(ad.essl_spendtime::text, ':'::text, 1)::numeric + replace(split_part(ad.essl_spendtime::text, ':'::text, 2), '0-'::text, '-'::text)::numeric / 60::numeric, 2)
            ELSE NULL::numeric
        END) AS dec_essl_spendtime
   FROM app_user a
     LEFT JOIN attendance ad ON a.id = ad.app_user_id
  GROUP BY a.id, (to_char(ad.date, 'dd'::text)), (to_char(ad.date, 'yyyy'::text))
  ORDER BY a.id, (to_char(ad.date, 'dd'::text)), (to_char(ad.date, 'yyyy'::text));

