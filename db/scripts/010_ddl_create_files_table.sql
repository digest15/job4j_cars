create table files (
    id serial primary key,
    name varchar not null,
    path varchar not null,
    post_id int references auto_post(id)
);