-- View: leave_history_vw

-- DROP VIEW leave_history_vw;

CREATE OR REPLACE VIEW leave_history_vw AS 
 SELECT a.id,
    to_char(dwal.leave_date, 'dd'::text)::bigint AS leave_day,
    to_char(dwal.leave_date, 'yyyy'::text) AS leave_year,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 1 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS jan_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 1 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS jan_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 2 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS feb_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 2 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS feb_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 3 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS mar_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 3 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS mar_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 4 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS apr_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 4 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS apr_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 5 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS may_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 5 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS may_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 6 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS jun_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 6 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS jun_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 7 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS jul_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 7 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS jul_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 8 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS aug_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 8 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS aug_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 9 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS sep_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 9 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS sep_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 10 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS oct_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 10 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS oct_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 11 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS nov_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 11 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS nov_applied_leave_type,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 12 THEN dwal.du_enum
            ELSE NULL::character varying
        END::text) AS dec_du_enum,
    max(
        CASE
            WHEN to_char(dwal.leave_date, 'mm'::text)::bigint = 12 THEN dwal.applied_leave_type
            ELSE NULL::character varying
        END::text) AS dec_applied_leave_type
   FROM date_wise_applied_leaves dwal
     LEFT JOIN applied_leaves al ON dwal.applied_leaves_id = al.id
     LEFT JOIN app_user a ON al.app_user_id = a.id
  WHERE a.status::text = 'Active'::text AND (al.leave_status::text = ANY (ARRAY['PENDING_APPROVAL'::character varying::text, 'APPROVED'::character varying::text]))
  GROUP BY a.id, (to_char(dwal.leave_date, 'dd'::text)::bigint), (to_char(dwal.leave_date, 'yyyy'::text))
  ORDER BY a.id, (to_char(dwal.leave_date, 'dd'::text)::bigint);


