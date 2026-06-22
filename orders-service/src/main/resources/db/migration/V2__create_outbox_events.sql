CREATE TABLE bounty.outbox_events (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    aggregate_type varchar(100) NOT NULL,
    aggregate_id uuid NOT NULL,
    event_type varchar(150) NOT NULL,
    payload jsonb NOT NULL,
    status varchar(30) DEFAULT 'PENDING' NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    last_error text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    processed_at timestamp with time zone,

    CONSTRAINT outbox_events_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_events_status_created
    ON bounty.outbox_events USING btree (status, created_at);

CREATE INDEX idx_outbox_events_aggregate
    ON bounty.outbox_events USING btree (aggregate_type, aggregate_id);
