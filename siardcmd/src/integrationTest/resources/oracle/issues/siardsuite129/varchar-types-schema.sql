ALTER SESSION SET CONTAINER=XEPDB1;

CREATE TABLE IT_USER.varchartest (
    id NUMBER PRIMARY KEY,
    text2 VARCHAR2(1),
    text3 VARCHAR2(255),
    text4 VARCHAR2(4000),
    day_of_year NUMBER(3),
    weight_in_kg NUMBER(3,2)
);

INSERT INTO IT_USER.varchartest (id, text2, text3, text4, day_of_year, weight_in_kg)
VALUES
    (1, 'b', 'Regular text', 'Another longer text example', 123, 1.32);

INSERT INTO IT_USER.varchartest (id, text2, text3, text4, day_of_year, weight_in_kg)
VALUES
    (2, 'd', 'Another text', 'Another longer text example', 25, 12.56);