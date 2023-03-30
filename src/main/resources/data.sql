DELETE FROM court;

DELETE FROM court;

DELETE FROM court;

INSERT INTO court (court_id, court_name) VALUES (1, 'Арбитражный суд Хабаровского края'), (2, 'Центральный районный суд г. Хабаровска'), (3, '0-не определен');

INSERT INTO representative (repr_id, name, password, isadmin, version) VALUES 
(1, 'Абрамов А.А.', '$2a$04$QPWEShK4bED5NgOqIGkWiOh.LgRG4T8xBVOE095oZS7bSUI2yGnU2', 1, 0),
(2, 'Бабкин Б.Б.',  '$2a$04$VxyBIV8U8/2qv1O7pIKPEeiCoTePJTMsVTf8rLFLQzhSlGOY5Pr.u', 0, 0);

INSERT INTO acase (case_id, relation, case_type, case_title, court, case_no, plaintiff, defendant, repr, stage, curr_date, curr_state, isarchive, version) VALUES
(1, 4, 4, 'ст. 286 ч.3 п.в УК РФ', 3, '2-22', NULL, 'Васильев В.В.', 1, 0, NULL, 'признаны потерпевшим', 0, 0),
(2, 1, 0, 'о взыскании 25 млн. руб.', 1, 'А100-17354/2020', 'Минфин России', 'Правительство края, Минстрой края', 2, 4, '2023-03-17 11:00:00', '23.11.2022 в удовлетворении кассационных жалоб отказано', 0, 6),
(3, 1, 0, 'О взыскании морального вреда', 1, '9-9999/2022', 'Прокурор в инт. Демидова Д.Д.', 'Наша компания', 1, 2, NULL, 'в требованиях к нашей компании отказано', 1, 0);

