CREATE TABLE `BeispielTabelle` (
                                   `ID` INT AUTO_INCREMENT PRIMARY KEY,
                                   `NAME` VARCHAR(50),
                                   `Alter` INT
);

INSERT INTO `BeispielTabelle` (`NAME`, `Alter`)
VALUES ('Max', 25),
       ('John', 30),
       ('Maria', 28);
