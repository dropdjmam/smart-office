ALTER TABLE administrating
    ADD CONSTRAINT FK_ADMINISTRATING_ON_OFFICE FOREIGN KEY (office_id) REFERENCES offices (id);