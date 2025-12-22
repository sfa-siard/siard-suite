CREATE DATABASE IF NOT EXISTS `simpledb`;
USE `simpledb`;

-- Create orders table
CREATE TABLE `orders` (
    `OrderID` INT AUTO_INCREMENT PRIMARY KEY,
    `CustomerName` VARCHAR(100),
    `OrderDate` DATE
);

-- Insert sample data
INSERT INTO `orders` (`CustomerName`, `OrderDate`)
VALUES ('John Doe', '2023-01-15'),
       ('Jane Smith', '2023-02-20'),
       ('Bob Johnson', '2023-03-10');

-- Create order details table with a foreign key constraint that has a space in its name
CREATE TABLE `order_details` (
    `DetailID` INT AUTO_INCREMENT PRIMARY KEY,
    `OrderID` INT,
    `ProductName` VARCHAR(100),
    `Quantity` INT,
    CONSTRAINT `Orders Order Details` FOREIGN KEY(`OrderID`) REFERENCES `orders`(`OrderID`)
);

-- Insert sample data
INSERT INTO `order_details` (`OrderID`, `ProductName`, `Quantity`)
VALUES (1, 'Product A', 5),
       (1, 'Product B', 3),
       (2, 'Product C', 2),
       (3, 'Product A', 1);
