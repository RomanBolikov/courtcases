CREATE TABLE IF NOT EXISTS acase (
	case_id IDENTITY,
	relation INT NOT NULL,
	case_type INT NOT NULL,
	case_title VARCHAR(300),
	court INT NOT NULL,
	case_no VARCHAR(20),
	plaintiff VARCHAR(200),
	defendant VARCHAR(200),
	repr INT,
	stage INT,
	curr_date TIMESTAMP,
	curr_state VARCHAR(300),
	isarchive INT NOT NULL,
	version INT
);

CREATE TABLE IF NOT EXISTS court (
	court_id IDENTITY,
	court_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS representative (
	repr_id IDENTITY,
	name VARCHAR(50) NOT NULL,
	password VARCHAR(72),
	isadmin INT NOT NULL,
	version INT
);

ALTER TABLE acase
add foreign key (court) references court(court_id);

ALTER TABLE acase
add foreign key (repr) references representative(repr_id);


	