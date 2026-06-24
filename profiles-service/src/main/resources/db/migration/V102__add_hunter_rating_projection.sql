ALTER TABLE bounty.hunter_profiles
    ADD COLUMN rating_count integer DEFAULT 0 NOT NULL;

ALTER TABLE bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_rating_count_check CHECK (rating_count >= 0);

UPDATE bounty.hunter_profiles
SET rating_count = completed_orders_count
WHERE average_rating > 0;

CREATE TABLE bounty.hunter_rating_events (
    review_id uuid NOT NULL,
    order_id uuid NOT NULL,
    hunter_profile_id uuid NOT NULL,
    rating integer NOT NULL,
    created_at timestamptz NOT NULL,
    processed_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT hunter_rating_events_pkey PRIMARY KEY (review_id),
    CONSTRAINT hunter_rating_events_rating_check CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT hunter_rating_events_hunter_profile_id_fkey
        FOREIGN KEY (hunter_profile_id) REFERENCES bounty.hunter_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_hunter_rating_events_hunter
    ON bounty.hunter_rating_events (hunter_profile_id, created_at DESC);
