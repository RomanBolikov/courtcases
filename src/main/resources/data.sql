INSERT INTO representative (repr_id, name, password, isadmin, version) 
VALUES (2, 'Абрамов А.А.', '$2a$04$/GBNY1tlLAc2JLqxi8LNeOZjV/FpO8mfJa6LSVSdhFpYNoAj0EwcG', 1, 0);
INSERT INTO representative (repr_id, name, password, isadmin, version) 
VALUES (3, 'Бабушкин Б.Б.', '$2a$04$/GBNY1tlLAc2JLqxi8LNeOZjV/FpO8mfJa6LSVSdhFpYNoAj0EwcG', 1, 0);
INSERT INTO representative (repr_id, name, password, isadmin, version) 
VALUES (4, 'Ведерников В.В.', NULL, 0, 0);
INSERT INTO representative (repr_id, name, password, isadmin, version) 
VALUES (6, 'Григорьев Г.Г.', NULL, 0, 0);
INSERT INTO representative (repr_id, name, password, isadmin, version) 
VALUES (20, 'Боликов Р.А.', '$2a$04$nhdyKctZkuMOJ1tWQC1CsOIxnhd2B0LgLDm7ixVYs0sgHH69oGVbC', 1, 1);


INSERT INTO court (court_id, court_name) VALUES (1, 'Центральный районный суд г. Хабаровска');
INSERT INTO court (court_id, court_name) VALUES (2, 'Арбитражный суд Хабаровского края');
INSERT INTO court (court_id, court_name) VALUES (3, 'Центральный районный суд г. Комсомольска-на-Амуре');
INSERT INTO court (court_id, court_name) VALUES (4, 'Ленинский районный суд г. Комсомольска-на-Амуре');
INSERT INTO court (court_id, court_name) VALUES (5, 'Ванинский районный суд');
INSERT INTO court (court_id, court_name) VALUES (6, 'Бикинский городской суд');
INSERT INTO court (court_id, court_name) VALUES (7, 'Амурский городской суд');
INSERT INTO court (court_id, court_name) VALUES (8, 'Верхнебуреинский районный суд');
INSERT INTO court (court_id, court_name) VALUES (9, 'Вяземский районный суд');
INSERT INTO court (court_id, court_name) VALUES (10, 'Краснофлотский районный суд г. Хабаровска');
INSERT INTO court (court_id, court_name) VALUES (11, 'Индустриальный районный суд г. Хабаровска');
INSERT INTO court (court_id, court_name) VALUES (12, 'Кировский районный суд г. Хабаровска');
INSERT INTO court (court_id, court_name) VALUES (13, 'Судебный участок № 24 Центр. р-на г. Хабаровска');
INSERT INTO court (court_id, court_name) VALUES (14, '-не определен');
INSERT INTO court (court_id, court_name) VALUES (15, 'Николаевский-на-Амуре городской суд');
INSERT INTO court (court_id, court_name) VALUES (16, 'Суд района им. Полины Осипенко');
INSERT INTO court (court_id, court_name) VALUES (17, 'Железнодорожный районный суд г. Хабаровска');
INSERT INTO court (court_id, court_name) VALUES (18, 'Хабаровский районный суд');
INSERT INTO court (court_id, court_name) VALUES (19, 'Суд района им. Лазо');
INSERT INTO court (court_id, court_name) VALUES (20, 'Хабаровский краевой суд');
INSERT INTO court (court_id, court_name) VALUES (21, 'Солнечный районный суд');


INSERT INTO acase (relation, case_type, case_title, court, case_no, plaintiff, defendant, repr, stage, 
					curr_date, curr_state, isarchive, version) 
VALUES (0, 1, 'Банкротство ООО "Звезда"', 2, 'А73-0000/2018', 'ООО "Завод"', 'ООО "Звезда"', 3, 1, 
			'2023-08-01 10:15:00', '01.08.2023 отчет конкурсного управляющего', 0, 1);
			




