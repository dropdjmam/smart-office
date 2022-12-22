ALTER TABLE bookings
    ALTER COLUMN guests DROP NOT NULL,
    ALTER COLUMN guests SET DEFAULT 0,
    ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

UPDATE bookings SET is_deleted = FALSE WHERE is_deleted IS NULL;

CREATE INDEX IF NOT EXISTS holder ON bookings (holder_id);

CREATE INDEX IF NOT EXISTS maker ON bookings (maker_id);

CREATE INDEX IF NOT EXISTS workplace_bookings ON bookings (workplace_id);

CREATE INDEX IF NOT EXISTS booking_end ON bookings (date_time_of_end);

CREATE INDEX IF NOT EXISTS booking_start ON bookings (date_time_of_start);

CREATE INDEX IF NOT EXISTS actual ON bookings (holder_id, date_time_of_start, date_time_of_end, is_deleted);

CREATE INDEX IF NOT EXISTS in_period ON bookings (workplace_id, date_time_of_start, date_time_of_end, is_deleted);

ALTER TABLE employees
    ALTER COLUMN role_id SET DEFAULT 1;

UPDATE employees SET role_id = 1 WHERE role_id IS NULL;
