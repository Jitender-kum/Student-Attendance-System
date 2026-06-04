SET @attendance_session_start_time_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND COLUMN_NAME = 'start_time'
);
SET @attendance_session_start_time_sql := IF(
    @attendance_session_start_time_exists = 0,
    'ALTER TABLE attendance_session ADD COLUMN start_time TIME NULL',
    'SELECT 1'
);
PREPARE attendance_session_start_time_stmt FROM @attendance_session_start_time_sql;
EXECUTE attendance_session_start_time_stmt;
DEALLOCATE PREPARE attendance_session_start_time_stmt;

SET @attendance_session_end_time_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND COLUMN_NAME = 'end_time'
);
SET @attendance_session_end_time_sql := IF(
    @attendance_session_end_time_exists = 0,
    'ALTER TABLE attendance_session ADD COLUMN end_time TIME NULL',
    'SELECT 1'
);
PREPARE attendance_session_end_time_stmt FROM @attendance_session_end_time_sql;
EXECUTE attendance_session_end_time_stmt;
DEALLOCATE PREPARE attendance_session_end_time_stmt;

SET @attendance_session_period_label_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND COLUMN_NAME = 'period_label'
);
SET @attendance_session_period_label_sql := IF(
    @attendance_session_period_label_exists = 0,
    'ALTER TABLE attendance_session ADD COLUMN period_label VARCHAR(50) NULL',
    'SELECT 1'
);
PREPARE attendance_session_period_label_stmt FROM @attendance_session_period_label_sql;
EXECUTE attendance_session_period_label_stmt;
DEALLOCATE PREPARE attendance_session_period_label_stmt;
