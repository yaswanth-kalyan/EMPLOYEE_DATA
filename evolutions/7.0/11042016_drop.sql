
DROP TABLE users_time_sheet;
DROP TABLE time_sheet;
DROP TABLE utime_sheet;
DROP TABLE project_time_sheet;
DROP TABLE ptime_sheet;

ALTER TABLE users_daily_report DROP COLUMN project_name
ALTER TABLE daily_report DROP COLUMN name

