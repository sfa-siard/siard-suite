CREATE USER 'it_user'@'%' IDENTIFIED BY 'it_password';
GRANT ALL PRIVILEGES ON test.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON Schema1.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON Schema2.* TO 'it_user'@'%';
FLUSH PRIVILEGES;
