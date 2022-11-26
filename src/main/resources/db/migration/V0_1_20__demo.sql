INSERT INTO employees(full_name, login, password, email, phone_number, role_id)
VALUES ('Ольга Ивановна Ли', 'oli', '$2y$10$M0UXBGSRGjRurkeIGMiejerU1mJPR4hH8SJEQqR5n.nh02xSReIpO',
        'oli@atb.su', '89149996622', 2),
       ('Иван Ильич Ким', 'ikim', '$2y$10$t71sUhQh87TU1ppEcJHJtOgzwm4jWKS/3ictrWnimHm2Ne44akvLS',
        'ikim@atb.su', '+79024325432', 1);

INSERT INTO cities(name)
VALUES ('Владивосток');

INSERT INTO offices(address, work_number, start_of_day, end_of_day, booking_range, city_id)
VALUES ('ул.Адмирала Фокина, д.4', '8(900)487-11-11', '09:00:00.000', '19:00:00.000', 7, 1),
       ('ул.Светланская, д.115', '8(999)456-21-12', '08:00:00.000', '22:00:00.000', 14, 1);

INSERT INTO floors(floor_number, map_floor, office_id)
VALUES (1, 'карта первого этажа', 1),
       (2, 'карта второго этажа', 1),
       (1, 'карта первого этажа', 2),
       (2, 'карта второго этажа', 2),
       (3, 'карта третьего этажа', 2);

INSERT INTO workplacetypes (name)
VALUES ('Одиночное место'),
       ('Переговорка');

INSERT INTO workplaces (workplacetype_id, floor_id, capacity)
VALUES (1, 1, 1),
       (1, 2, 1),
       (2, 1, 4),
       (2, 2, 8),
       (2, 3, 6),
       (1, 5, 1);

INSERT INTO bookings (maker_id, holder_id, workplace_id, date_time_of_start, date_time_of_end,
                      guests)
VALUES (1, 1, 1, '2022-11-13 11:10:00.000', '2022-11-13 16:30:00.000', 0),
       (2, 1, 2, '2022-11-24 10:00:00.000', '2022-11-24 12:00:00.000', 0),
       (2, 2, 3, '2022-11-25 16:30:00.000', '2022-11-25 19:00:00.000', 3),
       (2, 2, 4, '2022-11-29 10:30:00.000', '2022-11-29 13:50:00.000', 6),
       (2, 2, 5, '2022-11-28 14:00:00.000', '2022-11-28 18:40:00.000', 5),
       (1, 1, 6, '2022-11-30 12:00:00.000', '2022-11-30 17:30:00.000', 0);
