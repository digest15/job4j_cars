create table history_owner (
    id serial primary key,
    owner_id int not null references owners(id),
    car_id int not null references cars(id),
    start_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    end_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);