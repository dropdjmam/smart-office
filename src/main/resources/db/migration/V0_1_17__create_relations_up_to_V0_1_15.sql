ALTER TABLE offices
    ADD city_id BIGINT,
    ADD CONSTRAINT FK_OFFICES_ON_CITY FOREIGN KEY (city_id) REFERENCES cities (id);

ALTER TABLE floors
    ADD office_id BIGINT,
    ADD CONSTRAINT FK_FLOORS_ON_OFFICE FOREIGN KEY (office_id) REFERENCES offices (id);

ALTER TABLE workplaces
    ADD workplacetype_id BIGINT,
    ADD floor_id         BIGINT,
    ADD CONSTRAINT FK_WORKPLACES_ON_WORKPLACETYPE FOREIGN KEY (workplacetype_id) REFERENCES workplacetypes (id),
    ADD CONSTRAINT FK_WORKPLACES_ON_FLOOR FOREIGN KEY (floor_id) REFERENCES floors (id);

ALTER TABLE bookings
    ADD maker_id     BIGINT,
    ADD holder_id    BIGINT,
    ADD workplace_id BIGINT,
    ADD CONSTRAINT FK_BOOKINGS_ON_MAKER FOREIGN KEY (maker_id) REFERENCES employees (id),
    ADD CONSTRAINT FK_BOOKINGS_ON_HOLDER FOREIGN KEY (holder_id) REFERENCES employees (id),
    ADD CONSTRAINT FK_BOOKINGS_ON_WORKPLACE FOREIGN KEY (workplace_id) REFERENCES workplaces (id);
