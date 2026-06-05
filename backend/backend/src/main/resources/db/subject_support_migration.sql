SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS subject (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    department_id BIGINT NULL,
    course_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(60) NULL,
    description VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150) NULL,
    updated_by VARCHAR(150) NULL,
    CONSTRAINT fk_subject_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_subject_department FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id) REFERENCES course(id)
);

SET @attendance_session_subject_column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND COLUMN_NAME = 'subject_id'
);
SET @attendance_session_subject_column_sql := IF(
    @attendance_session_subject_column_exists = 0,
    'ALTER TABLE attendance_session ADD COLUMN subject_id BIGINT NULL',
    'SELECT 1'
);
PREPARE attendance_session_subject_column_stmt FROM @attendance_session_subject_column_sql;
EXECUTE attendance_session_subject_column_stmt;
DEALLOCATE PREPARE attendance_session_subject_column_stmt;

SET @attendance_session_teacher_index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND INDEX_NAME = 'idx_attendance_session_teacher_id'
);
SET @attendance_session_teacher_index_sql := IF(
    @attendance_session_teacher_index_exists = 0,
    'CREATE INDEX idx_attendance_session_teacher_id ON attendance_session (teacher_id)',
    'SELECT 1'
);
PREPARE attendance_session_teacher_index_stmt FROM @attendance_session_teacher_index_sql;
EXECUTE attendance_session_teacher_index_stmt;
DEALLOCATE PREPARE attendance_session_teacher_index_stmt;

SET @attendance_session_department_index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND INDEX_NAME = 'idx_attendance_session_department_id'
);
SET @attendance_session_department_index_sql := IF(
    @attendance_session_department_index_exists = 0,
    'CREATE INDEX idx_attendance_session_department_id ON attendance_session (department_id)',
    'SELECT 1'
);
PREPARE attendance_session_department_index_stmt FROM @attendance_session_department_index_sql;
EXECUTE attendance_session_department_index_stmt;
DEALLOCATE PREPARE attendance_session_department_index_stmt;

SET @attendance_session_course_index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND INDEX_NAME = 'idx_attendance_session_course_id'
);
SET @attendance_session_course_index_sql := IF(
    @attendance_session_course_index_exists = 0,
    'CREATE INDEX idx_attendance_session_course_id ON attendance_session (course_id)',
    'SELECT 1'
);
PREPARE attendance_session_course_index_stmt FROM @attendance_session_course_index_sql;
EXECUTE attendance_session_course_index_stmt;
DEALLOCATE PREPARE attendance_session_course_index_stmt;

SET @attendance_session_subject_index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND INDEX_NAME = 'idx_attendance_session_subject_id'
);
SET @attendance_session_subject_index_sql := IF(
    @attendance_session_subject_index_exists = 0,
    'CREATE INDEX idx_attendance_session_subject_id ON attendance_session (subject_id)',
    'SELECT 1'
);
PREPARE attendance_session_subject_index_stmt FROM @attendance_session_subject_index_sql;
EXECUTE attendance_session_subject_index_stmt;
DEALLOCATE PREPARE attendance_session_subject_index_stmt;

SET @attendance_session_subject_fk_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attendance_session'
      AND CONSTRAINT_NAME = 'fk_attendance_session_subject'
);
SET @attendance_session_subject_fk_sql := IF(
    @attendance_session_subject_fk_exists = 0,
    'ALTER TABLE attendance_session ADD CONSTRAINT fk_attendance_session_subject FOREIGN KEY (subject_id) REFERENCES subject(id)',
    'SELECT 1'
);
PREPARE attendance_session_subject_fk_stmt FROM @attendance_session_subject_fk_sql;
EXECUTE attendance_session_subject_fk_stmt;
DEALLOCATE PREPARE attendance_session_subject_fk_stmt;

SET FOREIGN_KEY_CHECKS = 1;
