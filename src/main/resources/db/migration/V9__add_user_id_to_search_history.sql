-- Adds a direct foreign key to app_user for search history records.
ALTER TABLE search_history
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

ALTER TABLE search_history
    ADD CONSTRAINT fk_search_history_user
        FOREIGN KEY (user_id) REFERENCES "app_user"(id);

UPDATE search_history
SET user_id = CASE
    WHEN user_identifier ~ '^[0-9]+$' THEN user_identifier::BIGINT
    ELSE NULL
END
WHERE user_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_search_history_user_id ON search_history(user_id);
CREATE INDEX IF NOT EXISTS idx_search_history_created_at ON search_history(created_at);
CREATE INDEX IF NOT EXISTS idx_search_history_search_type ON search_history(search_type);
