CREATE TABLE IF NOT EXISTS workplaces
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    capacity VARCHAR(255),
    CONSTRAINT pk_workplaces PRIMARY KEY (id)
);