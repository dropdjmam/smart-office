DELETE
FROM bookings;
DELETE
FROM workplaces;
DELETE
FROM floors;
DELETE
FROM offices;

UPDATE employees
SET password = '$2a$10$roq4.36fZQd6CFyCw/DtgOKvXGUXLxUf/L.uunI59GJtcq8.kV1Z2'
WHERE id = 1;
UPDATE employees
SET password = '$2a$10$SU7jtZPWJA85GLsJoThr9uHe6QMaM9gjefYwtGNp08/pjhMXPGp4G'
WHERE id = 2;

INSERT INTO employees(full_name, login, password, email, phone_number, role_id)
VALUES ('Михаил Юрьевич Чуйко', 'mchuiko',
        '$2a$10$kJGAyql.AvP1L7dSIWfAnOV26cezH6U8aivEq3FlPB4t9EQi613h6',
        'mchuiko@atb.su', '89998786612', 2),
       ('Александр Михайлович Мангутов', 'amangutov',
        '$2a$10$uKcug6HeY8c1uvJ3pTsqQ.p2SD6UETh56SZcEdRMaMfWwL0.noJSW',
        'amangutov@atb.su', '+79024863190', 1),
       ('Евгений Павлович Орлов', 'eorlov',
        '$2a$10$cZq4LfjS31DrCJXFgoQJrervguM.3QNwzOo2rbcayHy1jHedNbMXC',
        'eorlov@atb.su', '89240092714', 1),
       ('Сергей Максимович Писарев', 'spisarev',
        '$2a$10$P2hD2F78c79pyz5DyfP68Oip/oaYlo2gbsGMhzUS5nGjkUjrv4rIO',
        'spisarev@atb.su', '+79022473985', 2),
       ('Данила Игоревич Бровко', 'dbrovko',
        '$2a$10$IMGC5mawm0voDiYzQgcvdeuzo3g0v6u/nd1bTEwOZdhuoLrhKQetS',
        'dbrovko@atb.su', '89141337733', 1),
       ('Денис Алексеевич Ленинг', 'dlening',
        '$2a$10$a/EbqtRjAmYwQW6lObQr4uMEJfw9zjuMSYZotz.OiPqWnjXKNVMde',
        'dlening@atb.su', '89143739112', 2);

INSERT INTO teams (name, employee_id)
VALUES ('Digital Five', 3),
       ('Team Cringers', 8);

INSERT INTO team_members (employee_id, team_id)
VALUES (3, 1),
       (4, 1),
       (5, 1),
       (6, 1),
       (8, 2),
       (7, 2),
       (1, 2);

INSERT INTO cities(name, zone_id)
VALUES ('Благовещенск', 'Asia/Yakutsk');

INSERT INTO offices(address, work_number, start_of_day, end_of_day, booking_range, city_id)
VALUES ('ул.Окатовая, д.12', '8(999)423-52-15', '09:00:00.000', '20:00:00.000', 21, 1),
       ('ул.Гоголя, д.79', '8(999)429-92-52', '08:00:00.000', '22:00:00.000', 28, 2);

INSERT INTO administrating (employee_id, office_id)
VALUES (3, 3);

INSERT INTO floors(floor_number, office_id)
VALUES (1, 3),
       (2, 3),
       (3, 3),
       (1, 4);

INSERT INTO workplaces (capacity, workplacetype_id, floor_id, place_name)
VALUES (1, 1, 6, '№ 7'),
       (1, 1, 6, '№ 8'),
       (8, 2, 6, '№ 9'),
       (10, 2, 6, '№ 10');

INSERT INTO bookings (maker_id, holder_id, workplace_id, date_time_of_start, date_time_of_end,
                      guests)
VALUES (3, 3, 7, '2022-12-20 00:10:00.000', '2022-12-20 02:30:00.000', 0),
       (3, 3, 8, '2022-12-24 06:00:00.000', '2022-12-24 08:00:00.000', 0),
       (3, 3, 9, '2022-12-26 05:30:00.000', '2022-12-26 07:00:00.000', 5);
