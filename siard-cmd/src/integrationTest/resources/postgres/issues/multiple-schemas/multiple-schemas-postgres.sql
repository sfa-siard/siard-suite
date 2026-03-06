-- Reproduces the scenario from the issue:
-- A PostgreSQL database has several data schemas (s1, s2).
-- A view schema (views_schema) is created with views referencing the data schemas.
-- A restricted user (siard_user) is created that can ONLY access the view schema.
-- SIARD should be able to export only the view schema using the --schema option
-- without failing due to missing permissions on the data schemas.

-- Data schema s1
CREATE SCHEMA s1;

CREATE TABLE s1.customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(200)
);

INSERT INTO s1.customers (name, email) VALUES
    ('Alice', 'alice@example.com'),
    ('Bob', 'bob@example.com');

-- Data schema s2
CREATE SCHEMA s2;

CREATE TABLE s2.orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER,
    amount NUMERIC(10, 2)
);

INSERT INTO s2.orders (customer_id, amount) VALUES
    (1, 99.95),
    (2, 149.50);

-- View schema (the one the restricted user should access)
CREATE SCHEMA views_schema;

CREATE VIEW views_schema.v_customers AS
    SELECT id, name, email FROM s1.customers;

CREATE VIEW views_schema.v_orders AS
    SELECT id, customer_id, amount FROM s2.orders;

-- Restricted user: can only access the view schema, not s1 or s2
CREATE USER siard_user WITH PASSWORD 'siard_password';

GRANT CONNECT ON DATABASE test TO siard_user;
GRANT USAGE ON SCHEMA views_schema TO siard_user;
GRANT SELECT ON ALL TABLES IN SCHEMA views_schema TO siard_user;
