create table cars (
    id serial primary key,
    name varchar not null,
    engine_id int not null unique references engines(id)
);