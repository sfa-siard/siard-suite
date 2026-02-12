CREATE TYPE IF NOT EXISTS geo_coordinate_type AS (
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6)
);

CREATE TABLE IF NOT EXISTS location_data
(
    location_id   INT PRIMARY KEY,
    location_name VARCHAR(255),
    coordinates geo_coordinate_type
);

INSERT INTO location_data (location_id, location_name, coordinates)
VALUES (1, 'Central Park', (40.785091, -73.968285)),
       (2, 'Eiffel Tower', (48.858844, 2.294350)),
       (3, 'Sydney Opera House', (-33.856780, 151.215297));
