CREATE SCHEMA employee_data;


CREATE TABLE employee_data.test_table
(
    id          INT PRIMARY KEY,
    name        VARCHAR(100),
    description VARCHAR(MAX)
);

-- Insert some sample data
INSERT INTO employee_data.test_table (id, name, description)
VALUES (1, 'Test 1', 'This is a test');
INSERT INTO employee_data.test_table (id, name, description)
VALUES (2, 'Test 2', 'This is another test');

-- Table with underscore (job_history)
CREATE TABLE employee_data.job_history
(
    employee_id   INT PRIMARY KEY,
    start_date    DATE,
    end_date      DATE,
    job_id        VARCHAR(10),
    department_id INT
);

INSERT INTO employee_data.job_history (employee_id, start_date, end_date, job_id, department_id)
VALUES (101, '2020-01-01', '2021-01-01', 'IT_PROG', 60);
INSERT INTO employee_data.job_history (employee_id, start_date, end_date, job_id, department_id)
VALUES (102, '2019-05-15', '2020-12-31', 'MK_REP', 20);

-- Table without underscore for comparison
CREATE TABLE employee_data.employees
(
    employee_id INT PRIMARY KEY,
    first_name  VARCHAR(50),
    last_name   VARCHAR(50),
    email       VARCHAR(100),
    hire_date   DATE
);

INSERT INTO employee_data.employees (employee_id, first_name, last_name, email, hire_date)
VALUES (101, 'John', 'Smith', 'john.smith@example.com', '2018-05-21');
INSERT INTO employee_data.employees (employee_id, first_name, last_name, email, hire_date)
VALUES (102, 'Jane', 'Doe', 'jane.doe@example.com', '2019-03-15');
