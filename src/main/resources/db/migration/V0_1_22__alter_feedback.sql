ALTER TABLE feedbacks
    ADD IF NOT EXISTS title VARCHAR(255),
    ALTER COLUMN text TYPE VARCHAR(500),
    ALTER COLUMN time_stamp SET DEFAULT now() AT TIME ZONE 'UTC';

CREATE INDEX IF NOT EXISTS employee ON feedbacks (employee_id);