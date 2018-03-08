ALTER TABLE recruitment_selection_round ADD candidate_calendar_event_id character varying(255);
ALTER TABLE recruitment_selection_round ADD interviewer_calendar_event_id character varying(255);
ALTER TABLE recruitment_selection_round ADD google_drive_file_id character varying(255);
ALTER TABLE recruitment_selection_round ADD send_notification_flag boolean default false;