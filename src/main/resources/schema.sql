CREATE TABLE IF NOT EXISTS acase (
  case_id identity,
  relation tinyint NOT NULL,
  case_type tinyint NOT NULL,
  case_title varchar(300) NOT NULL,
  court integer NOT NULL,
  case_no varchar(20),
  plaintiff varchar(200),
  defendant varchar(200),
  repr integer,
  stage tinyint,
  curr_date timestamp,
  curr_state varchar(300),
  isarchive tinyint,
  version integer
);

CREATE TABLE IF NOT EXISTS court (
  court_id identity,
  court_name varchar(50)
);

CREATE TABLE IF NOT EXISTS representative (
  repr_id identity,
  name varchar(50),
  password varchar(72),
  isadmin tinyint,
  version integer
);

ALTER TABLE acase ADD FOREIGN KEY (court) REFERENCES court(court_id);

ALTER TABLE acase ADD FOREIGN KEY (repr) REFERENCES representative(repr_id);
