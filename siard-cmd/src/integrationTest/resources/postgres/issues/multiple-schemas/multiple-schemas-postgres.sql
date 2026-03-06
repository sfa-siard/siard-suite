-- Create schema1 with a table
CREATE SCHEMA schema1;

CREATE TABLE schema1.simple_table (
    id SERIAL PRIMARY KEY,
    value VARCHAR(100)
);

INSERT INTO schema1.simple_table (value) VALUES ('schema1_value1'), ('schema1_value2');

-- Create schema2 with a table
CREATE SCHEMA schema2;

CREATE TABLE schema2.simple_table (
    id SERIAL PRIMARY KEY,
    value VARCHAR(100)
);

INSERT INTO schema2.simple_table (value) VALUES ('schema2_value1'), ('schema2_value2');
