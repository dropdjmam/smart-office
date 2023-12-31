CREATE TABLE IF NOT EXISTS employees
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    full_name    VARCHAR(255),
    login        VARCHAR(255),
    password     VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255),
    photo        VARCHAR(255),
    CONSTRAINT pk_employees PRIMARY KEY (id)
);
