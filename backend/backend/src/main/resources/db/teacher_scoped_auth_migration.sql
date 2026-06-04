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

ALTER TABLE student
    ADD COLUMN IF NOT EXISTS teacher_id BIGINT NULL,
    ADD COLUMN IF NOT EXISTS department_id BIGINT NULL,
    ADD COLUMN IF NOT EXISTS course_id BIGINT NULL,
    ADD COLUMN IF NOT EXISTS gender VARCHAR(20) NULL,
    ADD COLUMN IF NOT EXISTS semester INT NULL;

ALTER TABLE student_attendance
    ADD COLUMN IF NOT EXISTS teacher_id BIGINT NULL;

ALTER TABLE student
    ADD CONSTRAINT fk_student_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id),
    ADD CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES department (id),
    ADD CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE student_attendance
    ADD CONSTRAINT fk_attendance_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id),
    ADD CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES student (id);

CREATE UNIQUE INDEX uk_student_teacher_roll_no ON student (teacher_id, roll_no);
CREATE UNIQUE INDEX uk_attendance_student_date ON student_attendance (student_id, attendance_date);

CREATE INDEX idx_student_teacher ON student (teacher_id);
CREATE INDEX idx_student_department ON student (department_id);
CREATE INDEX idx_student_course ON student (course_id);
CREATE INDEX idx_attendance_teacher_date ON student_attendance (teacher_id, attendance_date);

-- Legacy note:
-- Existing installations that previously used a combined year/semester value stored in student.year
-- should review and backfill student.semester as needed. The migration adds the new column without
-- destroying existing data so teams can map old values safely.
--
-- Existing student rows also need manual teacher/department/course assignment before enforcing
-- NOT NULL constraints on the new foreign-key columns in production.
