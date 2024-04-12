CREATE DATABASE siard;
GO

use siard
go

create table dbo.person
(
    id         int identity,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    age        int
)
go


insert into dbo.person (first_name, last_name, age)
    VALUES
    ('Max', 'Muster', 42),
    ('Peter', 'Müller', null)
go


