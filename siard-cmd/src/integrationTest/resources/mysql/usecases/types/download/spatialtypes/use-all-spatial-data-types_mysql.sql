-- Create a new user with restricted privileges
CREATE USER 'it_user'@'%' IDENTIFIED BY 'it_password';
GRANT ALL PRIVILEGES ON test.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON Schema1.* TO 'it_user'@'%';
FLUSH PRIVILEGES;

-- Create Schema1
CREATE SCHEMA IF NOT EXISTS Schema1;
USE Schema1;

CREATE TABLE SpatialDataTable
(
    id                        INT AUTO_INCREMENT PRIMARY KEY,
    point_column              POINT,
    linestring_column         LINESTRING,
    polygon_column            POLYGON,
    multipoint_column         MULTIPOINT,
    multilinestring_column    MULTILINESTRING,
    multipolygon_column       MULTIPOLYGON,
    geometry_column           GEOMETRY,
    geometrycollection_column GEOMETRYCOLLECTION
);

INSERT INTO SpatialDataTable (point_column,
                                linestring_column,
                                polygon_column,
                                multipoint_column,
                                multilinestring_column,
                                multipolygon_column,
                                geometry_column,
                                geometrycollection_column)
VALUES (ST_GeomFromText('POINT(1 1)'),
        ST_GeomFromText('LINESTRING(0 0, 1 1, 2 2)'),
        ST_GeomFromText('POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))'),
        ST_GeomFromText('MULTIPOINT((1 1), (2 2), (3 3))'),
        ST_GeomFromText('MULTILINESTRING((0 0, 1 1, 2 2), (3 3, 4 4, 5 5))'),
        ST_GeomFromText('MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)), ((2 2, 2 3, 3 3, 3 2, 2 2)))'),
        ST_GeomFromText('POINT(4 4)'),
        ST_GeomFromText('GEOMETRYCOLLECTION(POINT(5 5), LINESTRING(6 6, 7 7))'));


