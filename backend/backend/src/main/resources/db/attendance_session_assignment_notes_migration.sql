SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS attendance_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    department_id BIGINT NULL,
    course_id BIGINT NULL,
    session_date DATE NOT NULL,
    session_name VARCHAR(120) NOT NULL,
    subject_or_topic VARCHAR(255) NULL,
    remarks VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150) NULL,
    updated_by VARCHAR(150) NULL,
    CONSTRAINT fk_attendance_session_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_attendance_session_department FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_attendance_session_course FOREIGN KEY (course_id) REFERENCES course(id),
    UNIQUE KEY uk_attendance_session_teacher_scope (teacher_id, department_id, course_id, session_date, session_name)
);

CREATE TABLE IF NOT EXISTS attendance_entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    attendance_session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    marked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150) NULL,
    updated_by VARCHAR(150) NULL,
    CONSTRAINT fk_attendance_entry_session FOREIGN KEY (attendance_session_id) REFERENCES attendance_session(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_entry_student FOREIGN KEY (student_id) REFERENCES student(id),
    UNIQUE KEY uk_attendance_entry_session_student (attendance_session_id, student_id)
);

CREATE TABLE IF NOT EXISTS assignment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    department_id BIGINT NULL,
    course_id BIGINT NULL,
    title VARCHAR(180) NOT NULL,
    description VARCHAR(2000) NULL,
    due_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150) NULL,
    updated_by VARCHAR(150) NULL,
    CONSTRAINT fk_assignment_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_assignment_department FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_assignment_course FOREIGN KEY (course_id) REFERENCES course(id)
);

CREATE TABLE IF NOT EXISTS note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    department_id BIGINT NULL,
    course_id BIGINT NULL,
    title VARCHAR(180) NOT NULL,
    content VARCHAR(5000) NOT NULL,
    note_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150) NULL,
    updated_by VARCHAR(150) NULL,
    CONSTRAINT fk_note_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_note_department FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_note_course FOREIGN KEY (course_id) REFERENCES course(id)
);

INSERT INTO attendance_session (
    teacher_id,
    department_id,
    course_id,
    session_date,
    session_name,
    created_at,
    updated_at,
    created_by,
    updated_by
)
SELECT
    sa.teacher_id,
    s.department_id,
    s.course_id,
    sa.attendance_date,
    'Default',
    MIN(COALESCE(sa.created_on, NOW())),
    MAX(COALESCE(sa.updated_on, NOW())),
    MIN(sa.created_by),
    MAX(sa.updated_by)
FROM student_attendance sa
JOIN student s ON s.id = sa.student_id
LEFT JOIN attendance_session session_existing
    ON session_existing.teacher_id = sa.teacher_id
   AND session_existing.session_date = sa.attendance_date
   AND ((session_existing.department_id IS NULL AND s.department_id IS NULL) OR session_existing.department_id = s.department_id)
   AND ((session_existing.course_id IS NULL AND s.course_id IS NULL) OR session_existing.course_id = s.course_id)
   AND session_existing.session_name = 'Default'
WHERE sa.teacher_id IS NOT NULL
  AND session_existing.id IS NULL
GROUP BY
    sa.teacher_id,
    s.department_id,
    s.course_id,
    sa.attendance_date;

INSERT INTO attendance_entry (
    attendance_session_id,
    student_id,
    status,
    marked_at,
    updated_at,
    created_by,
    updated_by
)
SELECT
    session.id,
    sa.student_id,
    sa.attendance_status,
    COALESCE(sa.created_on, NOW()),
    COALESCE(sa.updated_on, NOW()),
    sa.created_by,
    sa.updated_by
FROM student_attendance sa
JOIN student s ON s.id = sa.student_id
JOIN attendance_session session
    ON session.teacher_id = sa.teacher_id
   AND session.session_date = sa.attendance_date
   AND ((session.department_id IS NULL AND s.department_id IS NULL) OR session.department_id = s.department_id)
   AND ((session.course_id IS NULL AND s.course_id IS NULL) OR session.course_id = s.course_id)
   AND session.session_name = 'Default'
LEFT JOIN attendance_entry entry_existing
    ON entry_existing.attendance_session_id = session.id
   AND entry_existing.student_id = sa.student_id
WHERE sa.teacher_id IS NOT NULL
  AND entry_existing.id IS NULL;

SET FOREIGN_KEY_CHECKS = 1;
