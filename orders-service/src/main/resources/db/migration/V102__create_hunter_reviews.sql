CREATE TABLE bounty.hunter_reviews (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    client_profile_id uuid NOT NULL,
    hunter_profile_id uuid NOT NULL,
    rating integer NOT NULL,
    comment varchar(2000),
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT hunter_reviews_pkey PRIMARY KEY (id),
    CONSTRAINT hunter_reviews_order_id_key UNIQUE (order_id),
    CONSTRAINT hunter_reviews_order_id_fkey
        FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE,
    CONSTRAINT hunter_reviews_rating_check CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_hunter_reviews_hunter_created
    ON bounty.hunter_reviews (hunter_profile_id, created_at DESC);

CREATE INDEX idx_hunter_reviews_client_created
    ON bounty.hunter_reviews (client_profile_id, created_at DESC);
