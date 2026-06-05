SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND COLUMN_NAME = 'semester');
SET @sql_stmt := IF(@col_exists = 0, 'ALTER TABLE student ADD COLUMN semester INT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_stmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Non-destructive backfill placeholder:
-- Existing deployments may have stored the old combined year/semester value in student.year.
-- Review legacy records and populate student.semester explicitly based on your academic rules.
