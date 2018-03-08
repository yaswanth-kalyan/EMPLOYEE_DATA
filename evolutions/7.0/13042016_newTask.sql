alter table task add column task_mark boolean;
alter table task alter column title type text;
update task set task_mark=false;

