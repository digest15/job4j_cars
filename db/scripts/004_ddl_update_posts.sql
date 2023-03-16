ALTER TABLE auto_post
    ADD COLUMN IF NOT EXISTS
        price_history_id int references price_history(id);