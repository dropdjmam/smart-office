ALTER TABLE workplaces
    ADD COLUMN IF NOT EXISTS place_name VARCHAR(255)
        DEFAULT concat('№ ', lastval()) NOT NULL;
