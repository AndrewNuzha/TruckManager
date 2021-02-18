CREATE DATABASE truck_manager_v1;
USE truck_manager_v1;

CREATE TABLE truck_manager_v1.locations (
  id bigint NOT NULL AUTO_INCREMENT,
  country varchar(30),
  city varchar(30),
  latitude double,
  longitude double,

  PRIMARY KEY (id)
);

CREATE TABLE truck_manager_v1.companies (
  id bigint NOT NULL AUTO_INCREMENT,
  balance decimal(11,2),
  name varchar(25),
  PRIMARY KEY (id)
);

CREATE TABLE truck_manager_v1.truck_details (
  id bigint NOT NULL AUTO_INCREMENT,
  mileage_before_service decimal(7,2),
  mileage decimal(9,2),
  fuel_consumption decimal(3,2),
  production_year timestamp(6),
  current_location_id bigint,
  PRIMARY KEY (id),
  FOREIGN KEY (current_location_id) REFERENCES truck_manager_v1.locations(id)
);

CREATE TABLE truck_manager_v1.trucks (
  id bigint NOT NULL AUTO_INCREMENT,
  model varchar(20),
  category varchar(20),
  max_load int,
  status varchar(20),
  company_id bigint,
  truck_details_id bigint,
  PRIMARY KEY (id),
  FOREIGN KEY (company_id) REFERENCES truck_manager_v1.companies(id),
  FOREIGN KEY (truck_details_id) REFERENCES truck_manager_v1.truck_details(id) ON DELETE CASCADE
);

CREATE TABLE truck_manager_v1.shipments (
  id bigint NOT NULL AUTO_INCREMENT,
  distance decimal(7,2),
  category varchar(20),
  income decimal(7,2),
  departure_time timestamp(6),
  departure_location_id bigint,
  arrival_location_id bigint,
  truck_id bigint,
  company_id bigint,
  PRIMARY KEY (id),
  FOREIGN KEY (departure_location_id) REFERENCES truck_manager_v1.locations(id),
  FOREIGN KEY (arrival_location_id) REFERENCES truck_manager_v1.locations(id),
  FOREIGN KEY (truck_id) REFERENCES truck_manager_v1.trucks(id),
  FOREIGN KEY (company_id) REFERENCES truck_manager_v1.companies(id)
);

CREATE TABLE truck_manager_v1.users (
  id bigint NOT NULL AUTO_INCREMENT,
  first_name varchar(20),
  last_name varchar(20),
  nick_name varchar(20),
  email varchar(30),
  password varchar(100),
  company_id bigint,
  PRIMARY KEY (id),
  FOREIGN KEY (company_id) REFERENCES truck_manager_v1.companies(id)
);

CREATE TABLE truck_manager_v1.logs (
  id bigint NOT NULL AUTO_INCREMENT,
  text_content varchar(500),
  log_time timestamp(6),
  user_id bigint,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES truck_manager_v1.users(id)
);

CREATE TABLE truck_manager_v1.roles (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(25),
  PRIMARY KEY (id)
);

CREATE TABLE truck_manager_v1.users_roles (
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES truck_manager_v1.users(id),
  FOREIGN KEY (role_id) REFERENCES truck_manager_v1.roles(id)
);

INSERT INTO truck_manager_v1.locations (
  country, city, latitude, longitude)
VALUES ('Russia', 'Saint-Petersburg', 59.939095, 30.315868),
('Russia', 'Moscow', 55.755814, 37.617635),
('Russia', 'Kazan', 55.796127, 49.106405);

CREATE UNIQUE INDEX nickname
    ON users(nick_name);

CREATE UNIQUE INDEX email
    ON users(email);

CREATE UNIQUE INDEX companyName
    ON companies(name);

CREATE UNIQUE INDEX city
    ON locations(city);