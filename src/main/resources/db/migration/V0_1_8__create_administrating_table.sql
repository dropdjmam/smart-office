CREATE TABLE IF NOT EXISTS administrating
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    employee_id BIGINT,
    office_id   BIGINT,
    CONSTRAINT pk_administrating PRIMARY KEY (id),
    CONSTRAINT FK_ADMINISTRATING_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES employees (id)
);