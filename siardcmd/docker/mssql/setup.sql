CREATE DATABASE testdb;
GO
use testdb;
GO
CREATE LOGIN testlogin WITH PASSWORD = 'Testloginpwd!';
GO
CREATE user testuser for login testlogin;
GO
