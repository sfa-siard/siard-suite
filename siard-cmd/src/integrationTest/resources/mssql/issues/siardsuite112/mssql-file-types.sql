CREATE SCHEMA FileTypes;

-- Create table to store all files
CREATE TABLE FileTypes.AllFiles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_data VARBINARY(MAX) NOT NULL
);

-- Create table for PDF files
CREATE TABLE FileTypes.PdfFiles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_data VARBINARY(MAX) NOT NULL
);

-- Create table for JPG files
CREATE TABLE FileTypes.JpgFiles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_data VARBINARY(MAX) NOT NULL
);