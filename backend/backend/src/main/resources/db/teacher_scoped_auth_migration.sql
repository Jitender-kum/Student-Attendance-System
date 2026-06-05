SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS student (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    father_name VARCHAR(255) NULL,
    mother_name VARCHAR(255) NULL,
    roll_no VARCHAR(255) NOT NULL,
    department VARCHAR(255) NULL,
    phone_number VARCHAR(255) NULL,
    email VARCHAR(255) NULL,
    address VARCHAR(255) NULL,
    father_phone_no VARCHAR(255) NULL,
    mother_phone_no VARCHAR(255) NULL,
    attendance_percentage DOUBLE NULL,
    course VARCHAR(255) NULL,
    father_occupation VARCHAR(255) NULL,
    year INT NULL,
    status BIT NULL,
    created_on DATETIME NULL,
    updated_on DATETIME NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL,
    CONSTRAINT pk_student PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student_attendance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    attendance_status VARCHAR(20) NOT NULL,
    created_by VARCHAR(255) NULL,
    created_on DATETIME NULL,
    updated_by VARCHAR(255) NULL,
    updated_on DATETIME NULL,
    CONSTRAINT pk_student_attendance PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS teacher (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT pk_teacher PRIMARY KEY (id),
    CONSTRAINT uk_teacher_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS department (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    teacher_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT pk_department PRIMARY KEY (id),
    CONSTRAINT uk_department_teacher_name UNIQUE (teacher_id, name),
    CONSTRAINT fk_department_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)
);

CREATE TABLE IF NOT EXISTS course (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(60) NULL,
    department_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT pk_course PRIMARY KEY (id),
    CONSTRAINT uk_course_teacher_code UNIQUE (teacher_id, code),
    CONSTRAINT fk_course_department FOREIGN KEY (department_id) REFERENCES department (id),
    CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)
);

-- Check and add student.teacher_id
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND COLUMN_NAME = 'teacher_id');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student ADD COLUMN teacher_id BIGINT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add student.department_id
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND COLUMN_NAME = 'department_id');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student ADD COLUMN department_id BIGINT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add student.course_id
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND COLUMN_NAME = 'course_id');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student ADD COLUMN course_id BIGINT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add student.gender
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND COLUMN_NAME = 'gender');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student ADD COLUMN gender VARCHAR(20) NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add student.semester
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND COLUMN_NAME = 'semester');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student ADD COLUMN semester INT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add student_attendance.teacher_id
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student_attendance' AND COLUMN_NAME = 'teacher_id');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student_attendance ADD COLUMN teacher_id BIGINT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add fk_student_teacher
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND CONSTRAINT_NAME = 'fk_student_teacher');
SET @sql_stmt := IF(@fk_exists = 0, 'ALTER TABLE student ADD CONSTRAINT fk_student_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add fk_student_department
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND CONSTRAINT_NAME = 'fk_student_department');
SET @sql_stmt := IF(@fk_exists = 0, 'ALTER TABLE student ADD CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES department (id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add fk_student_course
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND CONSTRAINT_NAME = 'fk_student_course');
SET @sql_stmt := IF(@fk_exists = 0, 'ALTER TABLE student ADD CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES course (id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add fk_attendance_teacher
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'student_attendance' AND CONSTRAINT_NAME = 'fk_attendance_teacher');
SET @sql_stmt := IF(@fk_exists = 0, 'ALTER TABLE student_attendance ADD CONSTRAINT fk_attendance_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add fk_attendance_student
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'student_attendance' AND CONSTRAINT_NAME = 'fk_attendance_student');
SET @sql_stmt := IF(@fk_exists = 0, 'ALTER TABLE student_attendance ADD CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES student (id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add uk_student_teacher_roll_no
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND INDEX_NAME = 'uk_student_teacher_roll_no');
SET @sql_stmt := IF(@idx_exists = 0, 'CREATE UNIQUE INDEX uk_student_teacher_roll_no ON student (teacher_id, roll_no)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add uk_attendance_student_date
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student_attendance' AND INDEX_NAME = 'uk_attendance_student_date');
SET @sql_stmt := IF(@idx_exists = 0, 'CREATE UNIQUE INDEX uk_attendance_student_date ON student_attendance (student_id, attendance_date)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add idx_student_teacher
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND INDEX_NAME = 'idx_student_teacher');
SET @sql_stmt := IF(@idx_exists = 0, 'CREATE INDEX idx_student_teacher ON student (teacher_id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add idx_student_department
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND INDEX_NAME = 'idx_student_department');
SET @sql_stmt := IF(@idx_exists = 0, 'CREATE INDEX idx_student_department ON student (department_id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add idx_student_course
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND INDEX_NAME = 'idx_student_course');
SET @sql_stmt := IF(@idx_exists = 0, 'CREATE INDEX idx_student_course ON student (course_id)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Check and add idx_attendance_teacher_date
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student_attendance' AND INDEX_NAME = 'idx_attendance_teacher_date');
SET @sql_stmt := IF(@idx_exists = 0, 'CREATE INDEX idx_attendance_teacher_date ON student_attendance (teacher_id, attendance_date)', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Legacy note:
-- Existing installations that previously used a combined year/semester value stored in student.year
-- should review and backfill student.semester as needed. The migration adds the new column without
-- destroying existing data so teams can map old values safely.
--
-- Existing student rows also need manual teacher/department/course assignment before enforcing
-- NOT NULL constraints on the new foreign-key columns in production.

SET FOREIGN_KEY_CHECKS = 1;
