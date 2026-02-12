ALTER SESSION SET CONTAINER=XEPDB1;

CREATE USER employee_data IDENTIFIED BY password QUOTA UNLIMITED ON USERS;

GRANT CONNECT, RESOURCE TO employee_data;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO employee_data;


CREATE TABLE employee_data.test_table
(
    id          NUMBER PRIMARY KEY,
    name        VARCHAR2(100),
    description CLOB
);

-- Insert some sample data
INSERT INTO employee_data.test_table (id, name, description)
VALUES (1, 'Test 1', 'This is a test');
INSERT INTO employee_data.test_table (id, name, description)
VALUES (2, 'Test 2', 'This is another test');

-- Table with underscore (job_history)
CREATE TABLE employee_data.job_history
(
    employee_id   NUMBER PRIMARY KEY,
    start_date    DATE,
    end_date      DATE,
    job_id        VARCHAR2(10),
    department_id NUMBER
);

INSERT INTO employee_data.job_history (employee_id, start_date, end_date, job_id, department_id)
VALUES (101, TO_DATE('2020-01-01', 'YYYY-MM-DD'), TO_DATE('2021-01-01', 'YYYY-MM-DD'), 'IT_PROG', 60);
INSERT INTO employee_data.job_history (employee_id, start_date, end_date, job_id, department_id)
VALUES (102, TO_DATE('2019-05-15', 'YYYY-MM-DD'), TO_DATE('2020-12-31', 'YYYY-MM-DD'), 'MK_REP', 20);

-- Table without underscore for comparison
CREATE TABLE employee_data.employees
(
    employee_id NUMBER PRIMARY KEY,
    first_name  VARCHAR2(50),
    last_name   VARCHAR2(50),
    email       VARCHAR2(100),
    hire_date   DATE
);

INSERT INTO employee_data.employees (employee_id, first_name, last_name, email, hire_date)
VALUES (101, 'John', 'Smith', 'john.smith@example.com', TO_DATE('2018-05-21', 'YYYY-MM-DD'));
INSERT INTO employee_data.employees (employee_id, first_name, last_name, email, hire_date)
VALUES (102, 'Jane', 'Doe', 'jane.doe@example.com', TO_DATE('2019-03-15', 'YYYY-MM-DD'));