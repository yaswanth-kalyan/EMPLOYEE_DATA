# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app_user (
  id                        bigserial not null,
  full_name                 varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  mobile_no                 bigint,
  gender                    varchar(6),
  organisation              varchar(255),
  job_title                 varchar(255),
  re_mgnr                   varchar(255),
  report_manger_id          bigint,
  joined_date               timestamp,
  dob                       timestamp,
  image                     bytea,
  thumbnail                 bytea,
  status                    varchar(9),
  is_password_change        boolean,
  login_check               boolean,
  git_id                    varchar(255),
  employee_id               varchar(255),
  essl_id                   bigint,
  experience                varchar(3),
  social_id                 TEXT,
  user_name                 varchar(255),
  constraint ck_app_user_gender check (gender in ('Male','Female')),
  constraint ck_app_user_status check (status in ('Active','Completed','Inactive')),
  constraint ck_app_user_experience check (experience in ('No','Yes')),
  constraint uq_app_user_full_name unique (full_name),
  constraint uq_app_user_email unique (email),
  constraint uq_app_user_mobile_no unique (mobile_no),
  constraint uq_app_user_essl_id unique (essl_id),
  constraint uq_app_user_user_name unique (user_name),
  constraint pk_app_user primary key (id))
;

create table applied_leaves (
  id                        bigserial not null,
  leave_status              varchar(16),
  leave_type_id             bigint,
  start_date                timestamp,
  end_date                  timestamp,
  total_leaves              float,
  reason                    TEXT,
  app_user_id               bigint,
  approved_by_id            bigint,
  year                      timestamp,
  rejected_reason           varchar(255),
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_applied_leaves_leave_status check (leave_status in ('CANCELLED','NOT_APPLIED','APLLIED','PENDING_APPROVAL','TAKEN','APPROVED','REJECTED')),
  constraint pk_applied_leaves primary key (id))
;

create table attachment (
  id                        bigserial not null,
  attachment_image          bytea,
  description               TEXT,
  title                     TEXT,
  url                       TEXT,
  attachment_type           varchar(8),
  app_user_id               bigint,
  image_url                 TEXT,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_attachment_attachment_type check (attachment_type in ('TEXTFILE','URL')),
  constraint pk_attachment primary key (id))
;

create table attendance (
  id                        bigserial not null,
  date                      timestamp,
  status                    varchar(7),
  in_time                   timestamp,
  out_time                  timestamp,
  spend_time                varchar(255),
  essl_intime               timestamp,
  essl_outtime              timestamp,
  essl_spendtime            varchar(255),
  time_in_office            varchar(255),
  essl_break_time           varchar(255),
  app_user_id               bigint,
  constraint ck_attendance_status check (status in ('WFH','CL','Present','Absent')),
  constraint pk_attendance primary key (id))
;

create table bb8landing_message (
  id                        bigserial not null,
  description               TEXT,
  added_by_id               bigint,
  constraint pk_bb8landing_message primary key (id))
;

create table biometric_attendance (
  id                        bigserial not null,
  essl_id                   bigint,
  date                      timestamp,
  status_code               integer,
  constraint pk_biometric_attendance primary key (id))
;

create table bug (
  id                        bigserial not null,
  title                     varchar(255) not null,
  description               text,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_bug primary key (id))
;

create table chat_app_user_last_seen_tab_info (
  id                        bigserial not null,
  logged_in_user_id         bigint not null,
  last_seen_tab             bigint not null,
  last_seen_tab_role        varchar(9) not null,
  previous_last_seen_tab    bigint,
  previous_last_seen_tab_role varchar(9),
  last_seen_date            timestamp,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_chat_app_user_last_seen_tab_info_last_seen_tab_role check (last_seen_tab_role in ('GROUP','BB8_ADMIN','USER')),
  constraint ck_chat_app_user_last_seen_tab_info_previous_last_seen_tab_role check (previous_last_seen_tab_role in ('GROUP','BB8_ADMIN','USER')),
  constraint uq_chat_app_user_last_seen_tab_i unique (logged_in_user_id),
  constraint pk_chat_app_user_last_seen_tab_i primary key (id))
;

create table chat_app_user_settings (
  id                        bigserial not null,
  logged_in_user_id         bigint not null,
  is_enable_desktop_notfication boolean not null,
  left_panel_color          varchar(255) not null,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint uq_chat_app_user_settings_logged unique (logged_in_user_id),
  constraint pk_chat_app_user_settings primary key (id))
;

create table chat_group (
  id                        bigserial not null,
  description               varchar(255),
  name                      varchar(255),
  group_type                varchar(7),
  created_by_id             bigint not null,
  is_disabled               boolean,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_chat_group_group_type check (group_type in ('PUBLIC','PRIVATE')),
  constraint pk_chat_group primary key (id))
;

create table chat_group_app_user_info (
  id                        bigserial not null,
  chat_group_id             bigint,
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_chat_group_app_user_info primary key (id))
;

create table client_contact_no (
  id                        bigserial not null,
  company_contacts_id       bigint not null,
  contact_type              varchar(6),
  contact_no                bigint,
  country_code              varchar(255),
  constraint ck_client_contact_no_contact_type check (contact_type in ('Work','Mobile','Home')),
  constraint pk_client_contact_no primary key (id))
;

create table company (
  id                        bigserial not null,
  company_name              varchar(255),
  address                   TEXT,
  website                   varchar(255),
  constraint uq_company_company_name unique (company_name),
  constraint pk_company primary key (id))
;

create table company_contact_info (
  id                        bigserial not null,
  company_id                bigint,
  company_contacts_id       bigint,
  job_title                 varchar(255),
  constraint pk_company_contact_info primary key (id))
;

create table company_contacts (
  id                        bigserial not null,
  contact_name              varchar(255),
  email_id                  varchar(255),
  location                  varchar(255),
  dob                       timestamp,
  anniversary_date          timestamp,
  constraint pk_company_contacts primary key (id))
;

create table contact (
  id                        bigserial not null,
  name                      varchar(255),
  moibile_no                varchar(255),
  email                     varchar(255),
  constraint pk_contact primary key (id))
;

create table daily_report (
  id                        serial not null,
  app_user_id               bigint,
  constraint pk_daily_report primary key (id))
;

create table date_wise_applied_leaves (
  id                        bigserial not null,
  applied_leaves_id         bigint not null,
  leave_date                timestamp,
  du_enum                   varchar(8),
  applied_leave_type        varchar(9),
  apply_user_id             bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_date_wise_applied_leaves_du_enum check (du_enum in ('HALF_DAY','FULL_DAY')),
  constraint ck_date_wise_applied_leaves_applied_leave_type check (applied_leave_type in ('Unplanned','Planned')),
  constraint pk_date_wise_applied_leaves primary key (id))
;

create table deduct_leave (
  id                        bigserial not null,
  applied_leaves_id         bigint not null,
  leave_type_id             bigint,
  deduct_leaves             float,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_deduct_leave primary key (id))
;

create table entitlement (
  id                        bigserial not null,
  leave_type_id             bigint,
  worked_date               varchar(255),
  leave_period              timestamp,
  no_of_days                float,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_entitlement primary key (id))
;

create table epic (
  id                        bigserial not null,
  name                      varchar(255) not null,
  description               varchar(255),
  road_map_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_epic primary key (id))
;

create table file_comment (
  id                        bigserial not null,
  comment                   TEXT,
  upload_file_info_id       bigint,
  comment_by_id             bigint,
  message_id                bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint uq_file_comment_message_id unique (message_id),
  constraint pk_file_comment primary key (id))
;

create table file_like (
  id                        bigserial not null,
  like_by_id                bigint,
  upload_file_info_id       bigint,
  created_on                timestamp not null,
  constraint pk_file_like primary key (id))
;

create table git_commit (
  id                        bigserial not null,
  git_notification_id       bigint not null,
  commit_id                 varchar(255),
  message                   TEXT,
  commit_url                TEXT,
  committed_at              timestamp,
  committer_name            varchar(255),
  committer_email           varchar(255),
  user_name                 varchar(255),
  constraint pk_git_commit primary key (id))
;

create table git_commit_comment (
  id                        bigserial not null,
  full_name                 varchar(255),
  commit_id                 varchar(255),
  comment_by                varchar(255),
  commit_url                varchar(255),
  comment                   TEXT,
  git_notification_id_id    bigint,
  constraint uq_git_commit_comment_git_notifi unique (git_notification_id_id),
  constraint pk_git_commit_comment primary key (id))
;

create table git_issue (
  id                        bigserial not null,
  title                     TEXT,
  comment                   TEXT,
  issue_url                 TEXT,
  git_notification_id_id    bigint,
  git_issue_type            varchar(8),
  issue_raised_by           varchar(255),
  issue_raised_by_url       TEXT,
  full_name                 varchar(255),
  issue_number              varchar(255),
  commented_by              varchar(255),
  assigned_to               varchar(255),
  assignee_url              TEXT,
  constraint ck_git_issue_git_issue_type check (git_issue_type in ('REOPEN','ASSIGNED','CLOSE','COMMENT','OPEN')),
  constraint uq_git_issue_git_notification_id unique (git_notification_id_id),
  constraint pk_git_issue primary key (id))
;

create table git_notification (
  id                        bigserial not null,
  notification_title        varchar(255),
  repository                varchar(255),
  repository_branch         varchar(255),
  no_of_commits             integer,
  committed_by              varchar(255),
  pusher_email              varchar(255),
  origin_json               TEXT,
  message_id                bigint,
  branched_from             varchar(255),
  repository_url            varchar(255),
  git_notification_type     varchar(24),
  constraint ck_git_notification_git_notification_type check (git_notification_type in ('CREATEBRANCH','DEPLOYMENT','FORK','GOLLUM','WATCH','DELETEBRANCH','DEPLOYMENTSTATUS','RELEASE','PAGEBUILD','MEMBER','ISSUES','TEAMADDED','PULLREQUEST','COMMITSTATUS','PUBLIC','PULLREQUESTREVIEWCOMMENT','COMMITCOMMENT','PUSH','ISSUECOMMENT')),
  constraint uq_git_notification_message_id unique (message_id),
  constraint pk_git_notification primary key (id))
;

create table holidays (
  id                        bigserial not null,
  year                      timestamp,
  holiday_for               varchar(255),
  holiday_date              timestamp,
  compensatory              boolean,
  corresponding_working_day timestamp,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_holidays primary key (id))
;

create table incident (
  id                        bigserial not null,
  incident_name             varchar(10),
  description               TEXT,
  image                     bytea,
  image_name                varchar(255),
  image_content_type        varchar(255),
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_incident_incident_name check (incident_name in ('Others','PMO','Sales','Engineer','Finance','HR','Operations','Marketing')),
  constraint pk_incident primary key (id))
;

create table interviewer_app_user (
  id                        bigserial not null,
  interviewer_id            bigint,
  constraint pk_interviewer_app_user primary key (id))
;

create table lead (
  id                        bigserial not null,
  company_id                bigint,
  app_user_id               bigint,
  opportunity_title         TEXT,
  opportunity_discription   TEXT,
  lead_source               varchar(17),
  estimated_amount          float,
  created_on                timestamp,
  last_update               timestamp,
  lead_status_id            bigint,
  constraint ck_lead_lead_source check (lead_source in ('Cold_Call','Word_of_Mouth','Employee','Self_Generated','Existing_Customer','Reference','Whatsapp_Groups','Partner','Conference','Other')),
  constraint pk_lead primary key (id))
;

create table lead_chat_comment (
  id                        bigserial not null,
  lead_id                   bigint,
  app_user_id               bigint,
  lead_status_id            bigint,
  comment                   TEXT,
  comment_date              timestamp,
  constraint pk_lead_chat_comment primary key (id))
;

create table lead_contact_info (
  id                        bigserial not null,
  lead_id                   bigint,
  company_contact_id        bigint,
  job_title                 varchar(255),
  constraint pk_lead_contact_info primary key (id))
;

create table lead_status (
  id                        bigserial not null,
  status                    varchar(255),
  description               TEXT,
  constraint pk_lead_status primary key (id))
;

create table leave_type (
  id                        bigserial not null,
  leave_type                varchar(255),
  carry_forward             boolean,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_leave_type primary key (id))
;

create table leaves (
  id                        bigserial not null,
  added_leaves              float,
  used_leaves               float,
  remaining_leaves          float,
  year                      timestamp,
  leave_type_id             bigint,
  leave_status              varchar(16),
  app_user_id               bigint,
  applied_leaves_id         bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_leaves_leave_status check (leave_status in ('CANCELLED','NOT_APPLIED','APLLIED','PENDING_APPROVAL','TAKEN','APPROVED','REJECTED')),
  constraint pk_leaves primary key (id))
;

create table mailing_list (
  id                        bigserial not null,
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_mailing_list primary key (id))
;

create table message (
  id                        bigserial not null,
  title                     varchar(255),
  description               TEXT,
  message_by_id             bigint not null,
  message_to_id             bigint,
  is_viewd                  boolean,
  message_content_type      varchar(15),
  role                      varchar(9),
  comments                  TEXT,
  is_attachment             boolean,
  random_id                 varchar(255),
  chat_group_id             bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_message_message_content_type check (message_content_type in ('RENAMEGROUP','LEFTGROUP','TEXT','GITNOTIFICATION','LEAVESTATUS','URL','COMMENT','ADDTOGROUP','DELETEGROUP','IMAGE','SNIPPET','CREATEGROUP','FILE','BIRTHDAY')),
  constraint ck_message_role check (role in ('GROUP','BB8_ADMIN','USER')),
  constraint pk_message primary key (id))
;

create table message_attachment (
  id                        bigserial not null,
  message_id                bigint,
  attachment_id             bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint uq_message_attachment_message_id unique (message_id),
  constraint uq_message_attachment_attachment unique (attachment_id),
  constraint pk_message_attachment primary key (id))
;

create table notification (
  id                        bigserial not null,
  message_by_id             bigint not null,
  message_to_id             bigint,
  to_chat_group_id          bigint,
  count                     bigint,
  is_viewed                 boolean,
  role                      varchar(9),
  message_id                bigint not null,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_notification_role check (role in ('GROUP','BB8_ADMIN','USER')),
  constraint pk_notification primary key (id))
;

create table notification_alert (
  id                        bigserial not null,
  notification              varchar(255),
  alert                     boolean,
  lead_id                   bigint,
  app_user_id               bigint,
  notification_date         timestamp,
  url                       varchar(255),
  notified_by_id            bigint,
  notified_to_id            bigint,
  role_id                   bigint,
  constraint pk_notification_alert primary key (id))
;

create table ptask (
  id                        bigserial not null,
  name                      varchar(255),
  description               text,
  estimated_time            Decimal(10,2),
  actual_time               Decimal(10,2),
  planned_start_date        timestamp,
  planned_end_date          timestamp,
  actual_start_date         timestamp,
  actual_end_date           timestamp,
  user_story_id             bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_ptask primary key (id))
;

create table page (
  id                        bigserial not null,
  title                     varchar(255) not null,
  is_active                 boolean,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_page primary key (id))
;

create table page_history (
  id                        bigserial not null,
  version                   integer,
  content                   text,
  page_id                   bigint,
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_page_history primary key (id))
;

create table pe_employee_appraisal (
  id                        bigserial not null,
  project_manager_id        bigint,
  project_team_member_id    bigint,
  month_date                timestamp,
  pr                        float,
  war                       float,
  issue                     TEXT,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_pe_employee_appraisal primary key (id))
;

create table pe_employee_appraisal_answer (
  id                        bigserial not null,
  pe_employee_appraisal_id  bigint not null,
  performance_question_id   bigint,
  rate                      bigint,
  answer                    TEXT,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_pe_employee_appraisal_answer primary key (id))
;

create table pe_question (
  id                        bigserial not null,
  question                  TEXT,
  note                      TEXT,
  weightage                 float,
  appraisal_type            varchar(18),
  question_status           varchar(9),
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_pe_question_appraisal_type check (appraisal_type in ('Employee_Appraisal','Self_Appraisal')),
  constraint ck_pe_question_question_status check (question_status in ('Active','Completed','Inactive')),
  constraint pk_pe_question primary key (id))
;

create table pe_self_appraisal (
  id                        bigserial not null,
  app_user_id               bigint,
  month_date                timestamp,
  saar                      float,
  war                       float,
  issue                     TEXT,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_pe_self_appraisal primary key (id))
;

create table pe_self_appraisal_answer (
  id                        bigserial not null,
  pe_self_appraisal_id      bigint not null,
  performance_question_id   bigint,
  rate                      bigint,
  answer                    TEXT,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_pe_self_appraisal_answer primary key (id))
;

create table policy (
  id                        bigserial not null,
  policy_name               TEXT,
  file                      bytea,
  file_name                 varchar(255),
  file_content_type         varchar(255),
  app_user_id               bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_policy primary key (id))
;

create table problems (
  id                        serial not null,
  users_daily_report_id     bigint not null,
  problem                   TEXT,
  constraint pk_problems primary key (id))
;

create table projects (
  id                        bigserial not null,
  project_name              varchar(255),
  description               varchar(255),
  started_date              timestamp,
  ended_date                timestamp,
  status                    varchar(9),
  client                    varchar(255),
  project_leader            varchar(255),
  project_manager_id        bigint,
  constraint ck_projects_status check (status in ('Active','Completed','Inactive')),
  constraint uq_projects_project_name unique (project_name),
  constraint pk_projects primary key (id))
;

create table recruitment_applicant (
  id                        bigserial not null,
  application_id            varchar(255),
  applicant_name            varchar(255),
  contact_no                bigint,
  email_id                  varchar(255),
  apply_date                timestamp,
  dob                       timestamp,
  refered_by_id             bigint,
  created_by_id             bigint,
  updated_by_id             bigint,
  applicant_category_id     bigint,
  recruitment_role_id       bigint,
  prefered_location         varchar(9),
  current_location          varchar(255),
  current_company           varchar(255),
  applicant_remark          TEXT,
  notice_period             float,
  current_ctc               float,
  expected_ctc              float,
  exprience                 float,
  recruitment_job_id        bigint,
  resume                    bytea,
  file_name                 varchar(255),
  file_content_type         varchar(255),
  recruitment_source_id     bigint,
  status                    varchar(16),
  send_intro_mail_flag      boolean,
  constraint ck_recruitment_applicant_prefered_location check (prefered_location in ('Hyderabad','Bangalore')),
  constraint ck_recruitment_applicant_status check (status in ('Shortlisted','Selected','Rejected','Offered','Joined','Registered','Abandoned','Offered_Accepted','NotJoined')),
  constraint pk_recruitment_applicant primary key (id))
;

create table recruitment_category (
  id                        bigserial not null,
  job_category_name         varchar(255),
  description               TEXT,
  constraint pk_recruitment_category primary key (id))
;

create table recruitment_interview_type (
  id                        bigserial not null,
  interview_type_name       varchar(255),
  description               TEXT,
  constraint pk_recruitment_interview_type primary key (id))
;

create table recruitment_interviewer_feedback (
  id                        bigserial not null,
  recruitment_applicant_id  bigint,
  recruitment_selection_round_id bigint,
  feed_back                 varchar(255),
  remark                    TEXT,
  interviewer_app_user_id   bigint,
  constraint pk_recruitment_interviewer_feedb primary key (id))
;

create table recruitment_job (
  id                        bigserial not null,
  job_id                    varchar(255),
  job_description           bytea,
  file_name                 varchar(255),
  file_content_type         varchar(255),
  recruitment_category_id   bigint,
  recruitment_role_id       bigint,
  job_experience            float,
  no_of_openning            integer,
  job_location              varchar(9),
  job_type                  varchar(9),
  open_date                 timestamp,
  last_date                 timestamp,
  job_status                varchar(7),
  remark                    TEXT,
  created_by_id             bigint,
  constraint ck_recruitment_job_job_location check (job_location in ('Hyderabad','Bangalore')),
  constraint ck_recruitment_job_job_type check (job_type in ('Full-Time','Part-Time','Contract')),
  constraint ck_recruitment_job_job_status check (job_status in ('Closed','Open','Defered')),
  constraint pk_recruitment_job primary key (id))
;

create table recruitment_mail_content (
  id                        bigserial not null,
  mail_type                 varchar(27),
  mail_content              TEXT,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_recruitment_mail_content_mail_type check (mail_type in ('Interview_Schedule_Email','Re_Schedule_Email','Intro_Email','Schedule_Email','Interview_Re_Schedule_Email')),
  constraint pk_recruitment_mail_content primary key (id))
;

create table recruitment_question_template (
  id                        bigserial not null,
  question_template_name    varchar(255),
  question_template         bytea,
  description               TEXT,
  filename                  varchar(255),
  file_content_type         varchar(255),
  constraint pk_recruitment_question_template primary key (id))
;

create table recruitment_reference (
  id                        bigserial not null,
  candidate_email           varchar(255),
  candidate_name            varchar(255),
  experience                float,
  resume                    bytea,
  resume_name               varchar(255),
  resume_content_tyep       varchar(255),
  refered_by_id             bigint,
  recruitment_job_id        bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint uq_recruitment_reference_candida unique (candidate_email),
  constraint pk_recruitment_reference primary key (id))
;

create table recruitment_role (
  id                        bigserial not null,
  job_role_name             varchar(255),
  description               TEXT,
  constraint pk_recruitment_role primary key (id))
;

create table recruitment_selection_round (
  id                        bigserial not null,
  recruitment_interview_type_id bigint,
  question_template_id      bigint,
  conduct_date              timestamp,
  to_date                   timestamp,
  feedback                  bytea,
  recruitment_applicant_id  bigint,
  time_reschedule           integer,
  remark                    varchar(255),
  selection_status          varchar(11),
  selection_result          varchar(8),
  interview_venue           varchar(13),
  send_mail_applicant_flag  boolean,
  send_mail_interviewer_flag boolean,
  send_notification_flag    boolean,
  candidate_calendar_event_id varchar(255),
  interviewer_calendar_event_id varchar(255),
  google_drive_file_id      varchar(255),
  constraint ck_recruitment_selection_round_selection_status check (selection_status in ('Scheduled','ReScheduled','Completed','Cancelled')),
  constraint ck_recruitment_selection_round_selection_result check (selection_result in ('NotSure','Selected','Rejected')),
  constraint ck_recruitment_selection_round_interview_venue check (interview_venue in ('Skype','Telephone','Thrymr_Office')),
  constraint pk_recruitment_selection_round primary key (id))
;

create table recruitment_skill (
  id                        bigserial not null,
  skill_name                varchar(255),
  description               TEXT,
  constraint pk_recruitment_skill primary key (id))
;

create table recruitment_source (
  id                        bigserial not null,
  source_name               varchar(255),
  description               TEXT,
  constraint pk_recruitment_source primary key (id))
;

create table road_map (
  id                        bigserial not null,
  title                     varchar(255) not null,
  description               text,
  project_id                bigint not null,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_road_map primary key (id))
;

create table role (
  id                        bigserial not null,
  role                      varchar(255),
  constraint uq_role_role unique (role),
  constraint pk_role primary key (id))
;

create table sprint (
  id                        bigserial not null,
  name                      varchar(255),
  start_date                timestamp,
  end_date                  timestamp,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_sprint primary key (id))
;

create table store_file (
  id                        bigserial not null,
  file                      bytea,
  file_name                 varchar(255),
  content_type              varchar(255),
  constraint pk_store_file primary key (id))
;

create table task (
  id                        bigserial not null,
  title                     TEXT not null,
  description               TEXT,
  assign_to_id              bigint,
  created_by_id             bigint,
  project_id                bigint,
  chat_group_id             bigint,
  creation_date             timestamp,
  status_id                 bigint,
  task_list_id              bigint,
  task_mark                 boolean,
  constraint pk_task primary key (id))
;

create table task_comment (
  id                        bigserial not null,
  app_user_id               bigint,
  comment                   TEXT,
  comment_date              timestamp,
  task_id                   bigint,
  task_status_id            bigint,
  constraint pk_task_comment primary key (id))
;

create table task_list (
  id                        bigserial not null,
  task_list_name            varchar(255),
  description               TEXT,
  created_by_id             bigint,
  creation_date             timestamp,
  constraint uq_task_list_task_list_name unique (task_list_name),
  constraint pk_task_list primary key (id))
;

create table task_status (
  id                        bigserial not null,
  status                    varchar(255),
  description               TEXT,
  constraint pk_task_status primary key (id))
;

create table test_case (
  id                        bigserial not null,
  name                      varchar(255),
  pre_conditions            varchar(255),
  descriptions              varchar(255),
  steps                     varchar(255),
  sample_input              varchar(255),
  expected_output           varchar(255),
  test_scenario_id          bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_test_case primary key (id))
;

create table test_execution (
  id                        bigserial not null,
  test_case_id              bigint,
  test_result               varchar(2),
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_test_execution_test_result check (test_result in ('O','P','NE','B','F')),
  constraint uq_test_execution_test_case_id unique (test_case_id),
  constraint pk_test_execution primary key (id))
;

create table test_run (
  id                        bigserial not null,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_test_run primary key (id))
;

create table test_scenario (
  id                        bigserial not null,
  user_story_id             bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_test_scenario primary key (id))
;

create table timesheet (
  id                        bigserial not null,
  app_user_id               bigint,
  project_id                bigint,
  hours                     float,
  date                      timestamp,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_timesheet primary key (id))
;

create table timesheet_user_remark (
  id                        bigserial not null,
  app_user_id               bigint,
  remark                    TEXT,
  date                      timestamp,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_timesheet_user_remark primary key (id))
;

create table todays (
  id                        serial not null,
  users_daily_report_id     bigint not null,
  today                     TEXT,
  constraint pk_todays primary key (id))
;

create table tomorrows (
  id                        serial not null,
  users_daily_report_id     bigint not null,
  tomorrow                  TEXT,
  constraint pk_tomorrows primary key (id))
;

create table upload_file_info (
  id                        bigserial not null,
  upload_image              bytea,
  upload_file_content_type  varchar(255),
  upload_file_name          varchar(255),
  file_size                 varchar(255),
  re_size_image             bytea,
  file_url                  varchar(255),
  app_user_id               bigint not null,
  git_issue_id              bigint,
  message_id                bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint uq_upload_file_info_message_id unique (message_id),
  constraint pk_upload_file_info primary key (id))
;

create table user_story (
  id                        bigserial not null,
  road_map_id               bigint not null,
  name                      varchar(255) not null,
  description               varchar(255),
  epic_id                   bigint not null,
  sprint_id                 bigint,
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint pk_user_story primary key (id))
;

create table users_daily_report (
  id                        bigserial not null,
  daily_report_id           integer not null,
  date                      timestamp,
  rate                      integer,
  is_done                   boolean,
  constraint pk_users_daily_report primary key (id))
;

create table working_days (
  id                        bigserial not null,
  day                       varchar(9),
  days_section              varchar(11),
  created_on                timestamp not null,
  last_update               timestamp not null,
  constraint ck_working_days_day check (day in ('WEDNESDAY','MONDAY','THURSDAY','SUNDAY','TUESDAY','FRIDAY','SATURDAY')),
  constraint ck_working_days_days_section check (days_section in ('HALF_DAY','FULL_DAY','NOT_WORKING')),
  constraint pk_working_days primary key (id))
;


create table AppUsers_Role (
  app_user_id                    bigint not null,
  role_id                        bigint not null,
  constraint pk_AppUsers_Role primary key (app_user_id, role_id))
;

create table AppUsers_Projects (
  app_user_id                    bigint not null,
  projects_id                    bigint not null,
  constraint pk_AppUsers_Projects primary key (app_user_id, projects_id))
;

create table appUser_Task_AllUser (
  app_user_id                    bigint not null,
  task_id                        bigint not null,
  constraint pk_appUser_Task_AllUser primary key (app_user_id, task_id))
;

create table taskList_appUsers (
  app_user_id                    bigint not null,
  task_list_id                   bigint not null,
  constraint pk_taskList_appUsers primary key (app_user_id, task_list_id))
;

create table entitlement_app_user (
  app_user_id                    bigint not null,
  entitlement_id                 bigint not null,
  constraint pk_entitlement_app_user primary key (app_user_id, entitlement_id))
;

create table Project_Client (
  contact_id                     bigint not null,
  projects_id                    bigint not null,
  constraint pk_Project_Client primary key (contact_id, projects_id))
;

create table recruitment_selection_round_interviewer (
  interviewer_app_user_id        bigint not null,
  recruitment_selection_round_id bigint not null,
  constraint pk_recruitment_selection_round_interviewer primary key (interviewer_app_user_id, recruitment_selection_round_id))
;

create table Lead_Comment (
  lead_id                        bigint not null,
  lead_chat_comment_id           bigint not null,
  constraint pk_Lead_Comment primary key (lead_id, lead_chat_comment_id))
;

create table Comment_File (
  lead_chat_comment_id           bigint not null,
  store_file_id                  bigint not null,
  constraint pk_Comment_File primary key (lead_chat_comment_id, store_file_id))
;

create table Lead_CCInfo (
  lead_contact_info_id           bigint not null,
  lead_id                        bigint not null,
  constraint pk_Lead_CCInfo primary key (lead_contact_info_id, lead_id))
;

create table recruitment_mandatoryskills (
  recruitment_job_id             bigint not null,
  recruitment_skill_id           bigint not null,
  constraint pk_recruitment_mandatoryskills primary key (recruitment_job_id, recruitment_skill_id))
;

create table recruitment_desiredskills (
  recruitment_job_id             bigint not null,
  recruitment_skill_id           bigint not null,
  constraint pk_recruitment_desiredskills primary key (recruitment_job_id, recruitment_skill_id))
;

create table appUser_Task_AddUser (
  task_id                        bigint not null,
  app_user_id                    bigint not null,
  constraint pk_appUser_Task_AddUser primary key (task_id, app_user_id))
;
alter table applied_leaves add constraint fk_applied_leaves_leaveType_1 foreign key (leave_type_id) references leave_type (id);
create index ix_applied_leaves_leaveType_1 on applied_leaves (leave_type_id);
alter table applied_leaves add constraint fk_applied_leaves_appUser_2 foreign key (app_user_id) references app_user (id);
create index ix_applied_leaves_appUser_2 on applied_leaves (app_user_id);
alter table applied_leaves add constraint fk_applied_leaves_approvedBy_3 foreign key (approved_by_id) references app_user (id);
create index ix_applied_leaves_approvedBy_3 on applied_leaves (approved_by_id);
alter table attachment add constraint fk_attachment_appUser_4 foreign key (app_user_id) references app_user (id);
create index ix_attachment_appUser_4 on attachment (app_user_id);
alter table attendance add constraint fk_attendance_appUser_5 foreign key (app_user_id) references app_user (id);
create index ix_attendance_appUser_5 on attendance (app_user_id);
alter table bb8landing_message add constraint fk_bb8landing_message_addedBy_6 foreign key (added_by_id) references app_user (id);
create index ix_bb8landing_message_addedBy_6 on bb8landing_message (added_by_id);
alter table chat_app_user_last_seen_tab_info add constraint fk_chat_app_user_last_seen_tab_7 foreign key (logged_in_user_id) references app_user (id);
create index ix_chat_app_user_last_seen_tab_7 on chat_app_user_last_seen_tab_info (logged_in_user_id);
alter table chat_app_user_settings add constraint fk_chat_app_user_settings_logg_8 foreign key (logged_in_user_id) references app_user (id);
create index ix_chat_app_user_settings_logg_8 on chat_app_user_settings (logged_in_user_id);
alter table chat_group add constraint fk_chat_group_createdBy_9 foreign key (created_by_id) references app_user (id);
create index ix_chat_group_createdBy_9 on chat_group (created_by_id);
alter table chat_group_app_user_info add constraint fk_chat_group_app_user_info_c_10 foreign key (chat_group_id) references chat_group (id);
create index ix_chat_group_app_user_info_c_10 on chat_group_app_user_info (chat_group_id);
alter table chat_group_app_user_info add constraint fk_chat_group_app_user_info_a_11 foreign key (app_user_id) references app_user (id);
create index ix_chat_group_app_user_info_a_11 on chat_group_app_user_info (app_user_id);
alter table client_contact_no add constraint fk_client_contact_no_company__12 foreign key (company_contacts_id) references company_contacts (id);
create index ix_client_contact_no_company__12 on client_contact_no (company_contacts_id);
alter table company_contact_info add constraint fk_company_contact_info_Compa_13 foreign key (company_id) references company (id);
create index ix_company_contact_info_Compa_13 on company_contact_info (company_id);
alter table company_contact_info add constraint fk_company_contact_info_compa_14 foreign key (company_contacts_id) references company_contacts (id);
create index ix_company_contact_info_compa_14 on company_contact_info (company_contacts_id);
alter table daily_report add constraint fk_daily_report_appUser_15 foreign key (app_user_id) references app_user (id);
create index ix_daily_report_appUser_15 on daily_report (app_user_id);
alter table date_wise_applied_leaves add constraint fk_date_wise_applied_leaves_a_16 foreign key (applied_leaves_id) references applied_leaves (id);
create index ix_date_wise_applied_leaves_a_16 on date_wise_applied_leaves (applied_leaves_id);
alter table date_wise_applied_leaves add constraint fk_date_wise_applied_leaves_a_17 foreign key (apply_user_id) references app_user (id);
create index ix_date_wise_applied_leaves_a_17 on date_wise_applied_leaves (apply_user_id);
alter table deduct_leave add constraint fk_deduct_leave_applied_leave_18 foreign key (applied_leaves_id) references applied_leaves (id);
create index ix_deduct_leave_applied_leave_18 on deduct_leave (applied_leaves_id);
alter table deduct_leave add constraint fk_deduct_leave_leaveType_19 foreign key (leave_type_id) references leave_type (id);
create index ix_deduct_leave_leaveType_19 on deduct_leave (leave_type_id);
alter table entitlement add constraint fk_entitlement_leaveType_20 foreign key (leave_type_id) references leave_type (id);
create index ix_entitlement_leaveType_20 on entitlement (leave_type_id);
alter table epic add constraint fk_epic_roadMap_21 foreign key (road_map_id) references road_map (id);
create index ix_epic_roadMap_21 on epic (road_map_id);
alter table file_comment add constraint fk_file_comment_uploadFileInf_22 foreign key (upload_file_info_id) references upload_file_info (id);
create index ix_file_comment_uploadFileInf_22 on file_comment (upload_file_info_id);
alter table file_comment add constraint fk_file_comment_commentBy_23 foreign key (comment_by_id) references app_user (id);
create index ix_file_comment_commentBy_23 on file_comment (comment_by_id);
alter table file_comment add constraint fk_file_comment_message_24 foreign key (message_id) references message (id);
create index ix_file_comment_message_24 on file_comment (message_id);
alter table file_like add constraint fk_file_like_likeBy_25 foreign key (like_by_id) references app_user (id);
create index ix_file_like_likeBy_25 on file_like (like_by_id);
alter table file_like add constraint fk_file_like_uploadFileInfo_26 foreign key (upload_file_info_id) references upload_file_info (id);
create index ix_file_like_uploadFileInfo_26 on file_like (upload_file_info_id);
alter table git_commit add constraint fk_git_commit_git_notificatio_27 foreign key (git_notification_id) references git_notification (id);
create index ix_git_commit_git_notificatio_27 on git_commit (git_notification_id);
alter table git_commit_comment add constraint fk_git_commit_comment_gitNoti_28 foreign key (git_notification_id_id) references git_notification (id);
create index ix_git_commit_comment_gitNoti_28 on git_commit_comment (git_notification_id_id);
alter table git_issue add constraint fk_git_issue_gitNotificationI_29 foreign key (git_notification_id_id) references git_notification (id);
create index ix_git_issue_gitNotificationI_29 on git_issue (git_notification_id_id);
alter table git_notification add constraint fk_git_notification_message_30 foreign key (message_id) references message (id);
create index ix_git_notification_message_30 on git_notification (message_id);
alter table incident add constraint fk_incident_appUser_31 foreign key (app_user_id) references app_user (id);
create index ix_incident_appUser_31 on incident (app_user_id);
alter table interviewer_app_user add constraint fk_interviewer_app_user_inter_32 foreign key (interviewer_id) references app_user (id);
create index ix_interviewer_app_user_inter_32 on interviewer_app_user (interviewer_id);
alter table lead add constraint fk_lead_company_33 foreign key (company_id) references company (id);
create index ix_lead_company_33 on lead (company_id);
alter table lead add constraint fk_lead_appUser_34 foreign key (app_user_id) references app_user (id);
create index ix_lead_appUser_34 on lead (app_user_id);
alter table lead add constraint fk_lead_leadStatus_35 foreign key (lead_status_id) references lead_status (id);
create index ix_lead_leadStatus_35 on lead (lead_status_id);
alter table lead_chat_comment add constraint fk_lead_chat_comment_leadStat_36 foreign key (lead_status_id) references lead_status (id);
create index ix_lead_chat_comment_leadStat_36 on lead_chat_comment (lead_status_id);
alter table lead_contact_info add constraint fk_lead_contact_info_lead_37 foreign key (lead_id) references lead (id);
create index ix_lead_contact_info_lead_37 on lead_contact_info (lead_id);
alter table lead_contact_info add constraint fk_lead_contact_info_companyC_38 foreign key (company_contact_id) references company_contacts (id);
create index ix_lead_contact_info_companyC_38 on lead_contact_info (company_contact_id);
alter table leaves add constraint fk_leaves_leaveType_39 foreign key (leave_type_id) references leave_type (id);
create index ix_leaves_leaveType_39 on leaves (leave_type_id);
alter table leaves add constraint fk_leaves_appUser_40 foreign key (app_user_id) references app_user (id);
create index ix_leaves_appUser_40 on leaves (app_user_id);
alter table leaves add constraint fk_leaves_appliedLeaves_41 foreign key (applied_leaves_id) references applied_leaves (id);
create index ix_leaves_appliedLeaves_41 on leaves (applied_leaves_id);
alter table mailing_list add constraint fk_mailing_list_appUser_42 foreign key (app_user_id) references app_user (id);
create index ix_mailing_list_appUser_42 on mailing_list (app_user_id);
alter table message add constraint fk_message_messageBy_43 foreign key (message_by_id) references app_user (id);
create index ix_message_messageBy_43 on message (message_by_id);
alter table message add constraint fk_message_messageTo_44 foreign key (message_to_id) references app_user (id);
create index ix_message_messageTo_44 on message (message_to_id);
alter table message add constraint fk_message_chatGroup_45 foreign key (chat_group_id) references chat_group (id);
create index ix_message_chatGroup_45 on message (chat_group_id);
alter table message_attachment add constraint fk_message_attachment_message_46 foreign key (message_id) references message (id);
create index ix_message_attachment_message_46 on message_attachment (message_id);
alter table message_attachment add constraint fk_message_attachment_attachm_47 foreign key (attachment_id) references attachment (id);
create index ix_message_attachment_attachm_47 on message_attachment (attachment_id);
alter table notification add constraint fk_notification_messageBy_48 foreign key (message_by_id) references app_user (id);
create index ix_notification_messageBy_48 on notification (message_by_id);
alter table notification add constraint fk_notification_messageTo_49 foreign key (message_to_id) references app_user (id);
create index ix_notification_messageTo_49 on notification (message_to_id);
alter table notification add constraint fk_notification_toChatGroup_50 foreign key (to_chat_group_id) references chat_group (id);
create index ix_notification_toChatGroup_50 on notification (to_chat_group_id);
alter table notification add constraint fk_notification_message_51 foreign key (message_id) references message (id);
create index ix_notification_message_51 on notification (message_id);
alter table notification_alert add constraint fk_notification_alert_notifie_52 foreign key (notified_by_id) references app_user (id);
create index ix_notification_alert_notifie_52 on notification_alert (notified_by_id);
alter table notification_alert add constraint fk_notification_alert_notifie_53 foreign key (notified_to_id) references app_user (id);
create index ix_notification_alert_notifie_53 on notification_alert (notified_to_id);
alter table notification_alert add constraint fk_notification_alert_role_54 foreign key (role_id) references role (id);
create index ix_notification_alert_role_54 on notification_alert (role_id);
alter table ptask add constraint fk_ptask_userStory_55 foreign key (user_story_id) references user_story (id);
create index ix_ptask_userStory_55 on ptask (user_story_id);
alter table page_history add constraint fk_page_history_page_56 foreign key (page_id) references page (id);
create index ix_page_history_page_56 on page_history (page_id);
alter table page_history add constraint fk_page_history_appUser_57 foreign key (app_user_id) references app_user (id);
create index ix_page_history_appUser_57 on page_history (app_user_id);
alter table pe_employee_appraisal add constraint fk_pe_employee_appraisal_proj_58 foreign key (project_manager_id) references app_user (id);
create index ix_pe_employee_appraisal_proj_58 on pe_employee_appraisal (project_manager_id);
alter table pe_employee_appraisal add constraint fk_pe_employee_appraisal_proj_59 foreign key (project_team_member_id) references app_user (id);
create index ix_pe_employee_appraisal_proj_59 on pe_employee_appraisal (project_team_member_id);
alter table pe_employee_appraisal_answer add constraint fk_pe_employee_appraisal_answ_60 foreign key (pe_employee_appraisal_id) references pe_employee_appraisal (id);
create index ix_pe_employee_appraisal_answ_60 on pe_employee_appraisal_answer (pe_employee_appraisal_id);
alter table pe_employee_appraisal_answer add constraint fk_pe_employee_appraisal_answ_61 foreign key (performance_question_id) references pe_question (id);
create index ix_pe_employee_appraisal_answ_61 on pe_employee_appraisal_answer (performance_question_id);
alter table pe_self_appraisal add constraint fk_pe_self_appraisal_appUser_62 foreign key (app_user_id) references app_user (id);
create index ix_pe_self_appraisal_appUser_62 on pe_self_appraisal (app_user_id);
alter table pe_self_appraisal_answer add constraint fk_pe_self_appraisal_answer_p_63 foreign key (pe_self_appraisal_id) references pe_self_appraisal (id);
create index ix_pe_self_appraisal_answer_p_63 on pe_self_appraisal_answer (pe_self_appraisal_id);
alter table pe_self_appraisal_answer add constraint fk_pe_self_appraisal_answer_p_64 foreign key (performance_question_id) references pe_question (id);
create index ix_pe_self_appraisal_answer_p_64 on pe_self_appraisal_answer (performance_question_id);
alter table policy add constraint fk_policy_appUser_65 foreign key (app_user_id) references app_user (id);
create index ix_policy_appUser_65 on policy (app_user_id);
alter table problems add constraint fk_problems_users_daily_repor_66 foreign key (users_daily_report_id) references users_daily_report (id);
create index ix_problems_users_daily_repor_66 on problems (users_daily_report_id);
alter table projects add constraint fk_projects_projectManager_67 foreign key (project_manager_id) references app_user (id);
create index ix_projects_projectManager_67 on projects (project_manager_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_refe_68 foreign key (refered_by_id) references app_user (id);
create index ix_recruitment_applicant_refe_68 on recruitment_applicant (refered_by_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_crea_69 foreign key (created_by_id) references app_user (id);
create index ix_recruitment_applicant_crea_69 on recruitment_applicant (created_by_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_upda_70 foreign key (updated_by_id) references app_user (id);
create index ix_recruitment_applicant_upda_70 on recruitment_applicant (updated_by_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_appl_71 foreign key (applicant_category_id) references recruitment_category (id);
create index ix_recruitment_applicant_appl_71 on recruitment_applicant (applicant_category_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_recr_72 foreign key (recruitment_role_id) references recruitment_role (id);
create index ix_recruitment_applicant_recr_72 on recruitment_applicant (recruitment_role_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_recr_73 foreign key (recruitment_job_id) references recruitment_job (id);
create index ix_recruitment_applicant_recr_73 on recruitment_applicant (recruitment_job_id);
alter table recruitment_applicant add constraint fk_recruitment_applicant_recr_74 foreign key (recruitment_source_id) references recruitment_source (id);
create index ix_recruitment_applicant_recr_74 on recruitment_applicant (recruitment_source_id);
alter table recruitment_interviewer_feedback add constraint fk_recruitment_interviewer_fe_75 foreign key (recruitment_applicant_id) references recruitment_applicant (id);
create index ix_recruitment_interviewer_fe_75 on recruitment_interviewer_feedback (recruitment_applicant_id);
alter table recruitment_interviewer_feedback add constraint fk_recruitment_interviewer_fe_76 foreign key (recruitment_selection_round_id) references recruitment_selection_round (id);
create index ix_recruitment_interviewer_fe_76 on recruitment_interviewer_feedback (recruitment_selection_round_id);
alter table recruitment_interviewer_feedback add constraint fk_recruitment_interviewer_fe_77 foreign key (interviewer_app_user_id) references interviewer_app_user (id);
create index ix_recruitment_interviewer_fe_77 on recruitment_interviewer_feedback (interviewer_app_user_id);
alter table recruitment_job add constraint fk_recruitment_job_recruitmen_78 foreign key (recruitment_category_id) references recruitment_category (id);
create index ix_recruitment_job_recruitmen_78 on recruitment_job (recruitment_category_id);
alter table recruitment_job add constraint fk_recruitment_job_recruitmen_79 foreign key (recruitment_role_id) references recruitment_role (id);
create index ix_recruitment_job_recruitmen_79 on recruitment_job (recruitment_role_id);
alter table recruitment_job add constraint fk_recruitment_job_createdBy_80 foreign key (created_by_id) references app_user (id);
create index ix_recruitment_job_createdBy_80 on recruitment_job (created_by_id);
alter table recruitment_reference add constraint fk_recruitment_reference_refe_81 foreign key (refered_by_id) references app_user (id);
create index ix_recruitment_reference_refe_81 on recruitment_reference (refered_by_id);
alter table recruitment_reference add constraint fk_recruitment_reference_recr_82 foreign key (recruitment_job_id) references recruitment_job (id);
create index ix_recruitment_reference_recr_82 on recruitment_reference (recruitment_job_id);
alter table recruitment_selection_round add constraint fk_recruitment_selection_roun_83 foreign key (recruitment_interview_type_id) references recruitment_interview_type (id);
create index ix_recruitment_selection_roun_83 on recruitment_selection_round (recruitment_interview_type_id);
alter table recruitment_selection_round add constraint fk_recruitment_selection_roun_84 foreign key (question_template_id) references recruitment_question_template (id);
create index ix_recruitment_selection_roun_84 on recruitment_selection_round (question_template_id);
alter table recruitment_selection_round add constraint fk_recruitment_selection_roun_85 foreign key (recruitment_applicant_id) references recruitment_applicant (id);
create index ix_recruitment_selection_roun_85 on recruitment_selection_round (recruitment_applicant_id);
alter table road_map add constraint fk_road_map_project_86 foreign key (project_id) references projects (id);
create index ix_road_map_project_86 on road_map (project_id);
alter table task add constraint fk_task_assignTo_87 foreign key (assign_to_id) references app_user (id);
create index ix_task_assignTo_87 on task (assign_to_id);
alter table task add constraint fk_task_createdBy_88 foreign key (created_by_id) references app_user (id);
create index ix_task_createdBy_88 on task (created_by_id);
alter table task add constraint fk_task_project_89 foreign key (project_id) references projects (id);
create index ix_task_project_89 on task (project_id);
alter table task add constraint fk_task_chatGroup_90 foreign key (chat_group_id) references chat_group (id);
create index ix_task_chatGroup_90 on task (chat_group_id);
alter table task add constraint fk_task_status_91 foreign key (status_id) references task_status (id);
create index ix_task_status_91 on task (status_id);
alter table task add constraint fk_task_taskList_92 foreign key (task_list_id) references task_list (id);
create index ix_task_taskList_92 on task (task_list_id);
alter table task_comment add constraint fk_task_comment_appUser_93 foreign key (app_user_id) references app_user (id);
create index ix_task_comment_appUser_93 on task_comment (app_user_id);
alter table task_comment add constraint fk_task_comment_task_94 foreign key (task_id) references task (id);
create index ix_task_comment_task_94 on task_comment (task_id);
alter table task_comment add constraint fk_task_comment_taskStatus_95 foreign key (task_status_id) references task_status (id);
create index ix_task_comment_taskStatus_95 on task_comment (task_status_id);
alter table task_list add constraint fk_task_list_createdBy_96 foreign key (created_by_id) references app_user (id);
create index ix_task_list_createdBy_96 on task_list (created_by_id);
alter table test_case add constraint fk_test_case_testScenario_97 foreign key (test_scenario_id) references test_scenario (id);
create index ix_test_case_testScenario_97 on test_case (test_scenario_id);
alter table test_execution add constraint fk_test_execution_testCase_98 foreign key (test_case_id) references test_case (id);
create index ix_test_execution_testCase_98 on test_execution (test_case_id);
alter table test_scenario add constraint fk_test_scenario_userStory_99 foreign key (user_story_id) references user_story (id);
create index ix_test_scenario_userStory_99 on test_scenario (user_story_id);
alter table timesheet add constraint fk_timesheet_appUser_100 foreign key (app_user_id) references app_user (id);
create index ix_timesheet_appUser_100 on timesheet (app_user_id);
alter table timesheet add constraint fk_timesheet_project_101 foreign key (project_id) references projects (id);
create index ix_timesheet_project_101 on timesheet (project_id);
alter table timesheet_user_remark add constraint fk_timesheet_user_remark_app_102 foreign key (app_user_id) references app_user (id);
create index ix_timesheet_user_remark_app_102 on timesheet_user_remark (app_user_id);
alter table todays add constraint fk_todays_users_daily_report_103 foreign key (users_daily_report_id) references users_daily_report (id);
create index ix_todays_users_daily_report_103 on todays (users_daily_report_id);
alter table tomorrows add constraint fk_tomorrows_users_daily_rep_104 foreign key (users_daily_report_id) references users_daily_report (id);
create index ix_tomorrows_users_daily_rep_104 on tomorrows (users_daily_report_id);
alter table upload_file_info add constraint fk_upload_file_info_appUser_105 foreign key (app_user_id) references app_user (id);
create index ix_upload_file_info_appUser_105 on upload_file_info (app_user_id);
alter table upload_file_info add constraint fk_upload_file_info_gitIssue_106 foreign key (git_issue_id) references git_issue (id);
create index ix_upload_file_info_gitIssue_106 on upload_file_info (git_issue_id);
alter table upload_file_info add constraint fk_upload_file_info_message_107 foreign key (message_id) references message (id);
create index ix_upload_file_info_message_107 on upload_file_info (message_id);
alter table user_story add constraint fk_user_story_road_map_108 foreign key (road_map_id) references road_map (id);
create index ix_user_story_road_map_108 on user_story (road_map_id);
alter table user_story add constraint fk_user_story_epic_109 foreign key (epic_id) references epic (id);
create index ix_user_story_epic_109 on user_story (epic_id);
alter table user_story add constraint fk_user_story_sprint_110 foreign key (sprint_id) references sprint (id);
create index ix_user_story_sprint_110 on user_story (sprint_id);
alter table users_daily_report add constraint fk_users_daily_report_daily__111 foreign key (daily_report_id) references daily_report (id);
create index ix_users_daily_report_daily__111 on users_daily_report (daily_report_id);



alter table AppUsers_Role add constraint fk_AppUsers_Role_app_user_01 foreign key (app_user_id) references app_user (id);

alter table AppUsers_Role add constraint fk_AppUsers_Role_role_02 foreign key (role_id) references role (id);

alter table AppUsers_Projects add constraint fk_AppUsers_Projects_app_user_01 foreign key (app_user_id) references app_user (id);

alter table AppUsers_Projects add constraint fk_AppUsers_Projects_projects_02 foreign key (projects_id) references projects (id);

alter table appUser_Task_AllUser add constraint fk_appUser_Task_AllUser_app_u_01 foreign key (app_user_id) references app_user (id);

alter table appUser_Task_AllUser add constraint fk_appUser_Task_AllUser_task_02 foreign key (task_id) references task (id);

alter table taskList_appUsers add constraint fk_taskList_appUsers_app_user_01 foreign key (app_user_id) references app_user (id);

alter table taskList_appUsers add constraint fk_taskList_appUsers_task_lis_02 foreign key (task_list_id) references task_list (id);

alter table entitlement_app_user add constraint fk_entitlement_app_user_app_u_01 foreign key (app_user_id) references app_user (id);

alter table entitlement_app_user add constraint fk_entitlement_app_user_entit_02 foreign key (entitlement_id) references entitlement (id);

alter table Project_Client add constraint fk_Project_Client_contact_01 foreign key (contact_id) references contact (id);

alter table Project_Client add constraint fk_Project_Client_projects_02 foreign key (projects_id) references projects (id);

alter table recruitment_selection_round_interviewer add constraint fk_recruitment_selection_roun_01 foreign key (interviewer_app_user_id) references interviewer_app_user (id);

alter table recruitment_selection_round_interviewer add constraint fk_recruitment_selection_roun_02 foreign key (recruitment_selection_round_id) references recruitment_selection_round (id);

alter table Lead_Comment add constraint fk_Lead_Comment_lead_01 foreign key (lead_id) references lead (id);

alter table Lead_Comment add constraint fk_Lead_Comment_lead_chat_com_02 foreign key (lead_chat_comment_id) references lead_chat_comment (id);

alter table Comment_File add constraint fk_Comment_File_lead_chat_com_01 foreign key (lead_chat_comment_id) references lead_chat_comment (id);

alter table Comment_File add constraint fk_Comment_File_store_file_02 foreign key (store_file_id) references store_file (id);

alter table Lead_CCInfo add constraint fk_Lead_CCInfo_lead_contact_i_01 foreign key (lead_contact_info_id) references lead_contact_info (id);

alter table Lead_CCInfo add constraint fk_Lead_CCInfo_lead_02 foreign key (lead_id) references lead (id);

alter table recruitment_mandatoryskills add constraint fk_recruitment_mandatoryskill_01 foreign key (recruitment_job_id) references recruitment_job (id);

alter table recruitment_mandatoryskills add constraint fk_recruitment_mandatoryskill_02 foreign key (recruitment_skill_id) references recruitment_skill (id);

alter table recruitment_desiredskills add constraint fk_recruitment_desiredskills__01 foreign key (recruitment_job_id) references recruitment_job (id);

alter table recruitment_desiredskills add constraint fk_recruitment_desiredskills__02 foreign key (recruitment_skill_id) references recruitment_skill (id);

alter table appUser_Task_AddUser add constraint fk_appUser_Task_AddUser_task_01 foreign key (task_id) references task (id);

alter table appUser_Task_AddUser add constraint fk_appUser_Task_AddUser_app_u_02 foreign key (app_user_id) references app_user (id);

# --- !Downs

drop table if exists app_user cascade;

drop table if exists AppUsers_Role cascade;

drop table if exists AppUsers_Projects cascade;

drop table if exists appUser_Task_AllUser cascade;

drop table if exists taskList_appUsers cascade;

drop table if exists entitlement_app_user cascade;

drop table if exists applied_leaves cascade;

drop table if exists attachment cascade;

drop table if exists attendance cascade;

drop table if exists bb8landing_message cascade;

drop table if exists biometric_attendance cascade;

drop table if exists bug cascade;

drop table if exists chat_app_user_last_seen_tab_info cascade;

drop table if exists chat_app_user_settings cascade;

drop table if exists chat_group cascade;

drop table if exists chat_group_app_user_info cascade;

drop table if exists client_contact_no cascade;

drop table if exists company cascade;

drop table if exists company_contact_info cascade;

drop table if exists company_contacts cascade;

drop table if exists contact cascade;

drop table if exists Project_Client cascade;

drop table if exists daily_report cascade;

drop table if exists date_wise_applied_leaves cascade;

drop table if exists deduct_leave cascade;

drop table if exists entitlement cascade;

drop table if exists epic cascade;

drop table if exists file_comment cascade;

drop table if exists file_like cascade;

drop table if exists git_commit cascade;

drop table if exists git_commit_comment cascade;

drop table if exists git_issue cascade;

drop table if exists git_notification cascade;

drop table if exists holidays cascade;

drop table if exists incident cascade;

drop table if exists interviewer_app_user cascade;

drop table if exists recruitment_selection_round_interviewer cascade;

drop table if exists lead cascade;

drop table if exists Lead_Comment cascade;

drop table if exists lead_chat_comment cascade;

drop table if exists Comment_File cascade;

drop table if exists lead_contact_info cascade;

drop table if exists Lead_CCInfo cascade;

drop table if exists lead_status cascade;

drop table if exists leave_type cascade;

drop table if exists leaves cascade;

drop table if exists mailing_list cascade;

drop table if exists message cascade;

drop table if exists message_attachment cascade;

drop table if exists notification cascade;

drop table if exists notification_alert cascade;

drop table if exists ptask cascade;

drop table if exists page cascade;

drop table if exists page_history cascade;

drop table if exists pe_employee_appraisal cascade;

drop table if exists pe_employee_appraisal_answer cascade;

drop table if exists pe_question cascade;

drop table if exists pe_self_appraisal cascade;

drop table if exists pe_self_appraisal_answer cascade;

drop table if exists policy cascade;

drop table if exists problems cascade;

drop table if exists projects cascade;

drop table if exists recruitment_applicant cascade;

drop table if exists recruitment_category cascade;

drop table if exists recruitment_interview_type cascade;

drop table if exists recruitment_interviewer_feedback cascade;

drop table if exists recruitment_job cascade;

drop table if exists recruitment_mandatoryskills cascade;

drop table if exists recruitment_desiredskills cascade;

drop table if exists recruitment_mail_content cascade;

drop table if exists recruitment_question_template cascade;

drop table if exists recruitment_reference cascade;

drop table if exists recruitment_role cascade;

drop table if exists recruitment_selection_round cascade;

drop table if exists recruitment_skill cascade;

drop table if exists recruitment_source cascade;

drop table if exists road_map cascade;

drop table if exists role cascade;

drop table if exists sprint cascade;

drop table if exists store_file cascade;

drop table if exists task cascade;

drop table if exists appUser_Task_AddUser cascade;

drop table if exists task_comment cascade;

drop table if exists task_list cascade;

drop table if exists task_status cascade;

drop table if exists test_case cascade;

drop table if exists test_execution cascade;

drop table if exists test_run cascade;

drop table if exists test_scenario cascade;

drop table if exists timesheet cascade;

drop table if exists timesheet_user_remark cascade;

drop table if exists todays cascade;

drop table if exists tomorrows cascade;

drop table if exists upload_file_info cascade;

drop table if exists user_story cascade;

drop table if exists users_daily_report cascade;

drop table if exists working_days cascade;

