CREATE TABLE IF NOT EXISTS offices
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    address       VARCHAR(255),
    work_number   VARCHAR(255),
    start_of_day  TIME WITHOUT TIME ZONE,
    end_of_day    TIME WITHOUT TIME ZONE,
    booking_range INTEGER,
    CONSTRAINT pk_offices PRIMARY KEY (id)
);
