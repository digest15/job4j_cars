ALTER TABLE auto_post ADD COLUMN if not exists car_id int not null unique REFERENCES cars(id);