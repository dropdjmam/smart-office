ALTER TABLE cities
    ADD COLUMN IF NOT EXISTS zone_id VARCHAR(255) DEFAULT 'UTC' NOT NULL;

SET TIME ZONE 'UTC';