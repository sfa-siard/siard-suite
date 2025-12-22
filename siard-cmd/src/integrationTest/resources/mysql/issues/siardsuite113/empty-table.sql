
create table public.table_name
(
    id    int auto_increment
        primary key,
    name  varchar(12) not null,
    int_1 int         not null,
    int_2 int         ,
    today date
);

-- insert into public.table_name (id, name, int_1, int_2, today) values (1, 'Max', 25, 30, null);
insert into public.table_name (id, name, int_1, int_2, today) values (2, 'Max', 25, null, '0000-00-00');