Alter table recruitment_applicant add column send_intro_mail_flag boolean DEFAULT false;
Alter table recruitment_selection_round add column send_mail_applicant_flag boolean DEFAULT false,add column send_mail_interviewer_flag boolean DEFAULT false;