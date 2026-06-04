SET @attendance_session_scope_index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND INDEX_NAME = 'uk_attendance_session_teacher_scope'
);
SET @attendance_session_scope_index_drop_sql := IF(
    @attendance_session_scope_index_exists > 0,
    'DROP INDEX uk_attendance_session_teacher_scope ON attendance_session',
    'SELECT 1'
);
PREPARE attendance_session_scope_index_drop_stmt FROM @attendance_session_scope_index_drop_sql;
EXECUTE attendance_session_scope_index_drop_stmt;
DEALLOCATE PREPARE attendance_session_scope_index_drop_stmt;

CREATE UNIQUE INDEX uk_attendance_session_teacher_scope
    ON attendance_session (
        teacher_id,
        department_id,
        course_id,
        subject_id,
        session_date,
        session_name,
        start_time,
        end_time
    );
