CREATE TABLE post_subscriptions (
   id serial PRIMARY KEY,
   user_id int not null REFERENCES auto_user(id),
   post_id int not null REFERENCES auto_post(id)
);