USE public;

-- Create table to store all files
CREATE TABLE all_files
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    filename  VARCHAR(255) NOT NULL,
    file_type VARCHAR(50)  NOT NULL,
    file_data LONGBLOB     NOT NULL
);

-- Create table for PDF files
CREATE TABLE pdf_files
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    filename  VARCHAR(255) NOT NULL,
    file_data LONGBLOB     NOT NULL
);

-- Create table for JPG files
CREATE TABLE jpg_files
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    filename  VARCHAR(255) NOT NULL,
    file_data LONGBLOB     NOT NULL
);
