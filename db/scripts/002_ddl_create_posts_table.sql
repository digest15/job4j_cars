create table auto_post (
    id              SERIAL PRIMARY KEY,
    description     varchar not null,
    creation_date   timestamp,
    auto_user_id    int references auto_user(id)
);