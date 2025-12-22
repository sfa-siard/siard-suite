CREATE TABLE BeispielTabelle
(
    "ID"    SERIAL PRIMARY KEY,
    "Name"  VARCHAR(50) COLLATE "de_DE.utf8",
    "name"  VARCHAR(50) COLLATE "de_DE.utf8",
    "Alter" INT
);

INSERT INTO BeispielTabelle ("Name", "name", "Alter")
VALUES ('Max', 'max', 25),
       ('John', 'john', 30),
       ('Maria', 'maria', 28);