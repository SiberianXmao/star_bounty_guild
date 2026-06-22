CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS bounty;

CREATE TABLE bounty.notifications (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    event_id uuid NOT NULL,
    event_type varchar(150) NOT NULL,
    type varchar(80) NOT NULL,
    recipient_type varchar(80) NOT NULL,
    recipient_id uuid NOT NULL,
    title varchar(200) NOT NULL,
    message text NOT NULL,
    related_order_id uuid,
    payload jsonb NOT NULL,
    consumed_at timestamp with time zone DEFAULT now() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT notifications_pkey PRIMARY KEY (id),
    CONSTRAINT ux_notifications_event_id UNIQUE (event_id)
);

CREATE INDEX idx_notifications_recipient_created
    ON bounty.notifications USING btree (recipient_type, recipient_id, created_at DESC);

CREATE INDEX idx_notifications_related_order
    ON bounty.notifications USING btree (related_order_id);
