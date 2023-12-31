ALTER TABLE employees
    ADD role_id BIGINT,
    ADD CONSTRAINT FK_EMPLOYEES_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE feedbacks
    ADD employee_id BIGINT,
    ADD CONSTRAINT FK_FEEDBACKS_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES employees (id);

ALTER TABLE teams
    ADD employee_id BIGINT,
    ADD CONSTRAINT FK_TEAMS_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES employees (id);
