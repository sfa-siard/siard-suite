-- Create it_user with privileges for nation and test databases
CREATE USER 'it_user'@'%' IDENTIFIED BY 'it_password';
GRANT ALL PRIVILEGES ON wildcard_schema_test.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON wildcardAschemaBtest.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON test.* TO 'it_user'@'%';

-- Schema creation - with underscore in the name
CREATE SCHEMA IF NOT EXISTS wildcard_schema_test;
USE wildcard_schema_test;

CREATE TABLE wildcard_table_test
(
    id     INT PRIMARY KEY,
    text_1 VARCHAR(1),
    textA1 VARCHAR(1),
    text_2 VARCHAR(255)
);

CREATE TABLE wildcardAtableBtest
(
    id     INT PRIMARY KEY,
    text_1 VARCHAR(1),
    textA1 VARCHAR(255)
);

-- Second schema creation
CREATE SCHEMA IF NOT EXISTS wildcardAschemaBtest;
USE wildcardAschemaBtest;

CREATE TABLE wildcard_table_test_2
(
    id   INT PRIMARY KEY,
    text VARCHAR(100)
);