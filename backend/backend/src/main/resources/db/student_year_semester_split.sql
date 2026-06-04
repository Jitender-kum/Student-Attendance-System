ALTER TABLE student
    ADD COLUMN IF NOT EXISTS semester INT NULL;

-- Non-destructive backfill placeholder:
-- Existing deployments may have stored the old combined year/semester value in student.year.
-- Review legacy records and populate student.semester explicitly based on your academic rules.
