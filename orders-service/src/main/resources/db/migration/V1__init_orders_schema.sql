CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS bounty;

CREATE TYPE bounty.acceptance_mode AS ENUM (
    'APPLICATIONS',
    'INSTANT_ACCEPT',
    'PERSONAL_OFFER'
);

CREATE TYPE bounty.application_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'REJECTED',
    'WITHDRAWN'
);

CREATE TYPE bounty.offer_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'DECLINED',
    'EXPIRED',
    'CANCELLED'
);

CREATE TYPE bounty.order_status AS ENUM (
    'DRAFT',
    'OPEN',
    'OFFERED',
    'ASSIGNED',
    'IN_PROGRESS',
    'SUBMITTED',
    'COMPLETED',
    'CANCELLED',
    'DISPUTED',
    'ARCHIVED'
);

CREATE TYPE bounty.order_visibility AS ENUM (
    'PUBLIC',
    'PRIVATE'
);

CREATE TYPE bounty.risk_level AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'EXTREME'
);

CREATE TYPE bounty.urgency_level AS ENUM (
    'LOW',
    'NORMAL',
    'HIGH',
    'CRITICAL'
);

CREATE TABLE bounty.orders (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    client_id uuid NOT NULL,
    assigned_hunter_id uuid,
    title varchar(200) NOT NULL,
    description text NOT NULL,
    category_id uuid NOT NULL,
    reward_amount numeric(14,2) NOT NULL,
    reward_currency_code varchar(16) NOT NULL,
    planet_id uuid,
    sector_id uuid,
    risk_level bounty.risk_level DEFAULT 'MEDIUM'::bounty.risk_level NOT NULL,
    urgency_level bounty.urgency_level DEFAULT 'NORMAL'::bounty.urgency_level NOT NULL,
    status bounty.order_status DEFAULT 'DRAFT'::bounty.order_status NOT NULL,
    visibility bounty.order_visibility DEFAULT 'PUBLIC'::bounty.order_visibility NOT NULL,
    acceptance_mode bounty.acceptance_mode DEFAULT 'APPLICATIONS'::bounty.acceptance_mode NOT NULL,
    requirements text,
    deadline timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    published_at timestamp with time zone,
    completed_at timestamp with time zone,

    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT orders_completed_has_timestamp_check
        CHECK ((status <> 'COMPLETED'::bounty.order_status) OR (completed_at IS NOT NULL)),
    CONSTRAINT orders_reward_amount_check CHECK (reward_amount >= 0)
);

CREATE TABLE bounty.order_applications (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    hunter_id uuid NOT NULL,
    message text,
    proposed_reward numeric(14,2),
    status bounty.application_status DEFAULT 'PENDING'::bounty.application_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,

    CONSTRAINT order_applications_pkey PRIMARY KEY (id),
    CONSTRAINT order_applications_order_hunter_key UNIQUE (order_id, hunter_id),
    CONSTRAINT order_applications_proposed_reward_check
        CHECK ((proposed_reward IS NULL) OR (proposed_reward >= 0)),
    CONSTRAINT order_applications_order_id_fkey
        FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE
);

CREATE TABLE bounty.order_offers (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    client_id uuid NOT NULL,
    hunter_id uuid NOT NULL,
    message text,
    status bounty.offer_status DEFAULT 'PENDING'::bounty.offer_status NOT NULL,
    expires_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,

    CONSTRAINT order_offers_pkey PRIMARY KEY (id),
    CONSTRAINT order_offers_order_hunter_key UNIQUE (order_id, hunter_id),
    CONSTRAINT order_offers_order_id_fkey
        FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_orders_status ON bounty.orders USING btree (status);
CREATE INDEX idx_orders_client_id ON bounty.orders USING btree (client_id);
CREATE INDEX idx_orders_assigned_hunter_id ON bounty.orders USING btree (assigned_hunter_id);
CREATE INDEX idx_orders_category_id ON bounty.orders USING btree (category_id);
CREATE INDEX idx_orders_planet_id ON bounty.orders USING btree (planet_id);
CREATE INDEX idx_orders_sector_id ON bounty.orders USING btree (sector_id);
CREATE INDEX idx_orders_risk_level ON bounty.orders USING btree (risk_level);
CREATE INDEX idx_orders_urgency_level ON bounty.orders USING btree (urgency_level);
CREATE INDEX idx_orders_reward_amount ON bounty.orders USING btree (reward_amount);
CREATE INDEX idx_orders_published_at ON bounty.orders USING btree (published_at);
CREATE INDEX idx_orders_deadline ON bounty.orders USING btree (deadline);
CREATE INDEX idx_orders_search
    ON bounty.orders USING gin (
        to_tsvector(
            'simple'::regconfig,
            (COALESCE(title, '') || ' ' || COALESCE(description, ''))
        )
    );

CREATE INDEX idx_order_applications_order_id ON bounty.order_applications USING btree (order_id);
CREATE INDEX idx_order_applications_hunter_id ON bounty.order_applications USING btree (hunter_id);
CREATE INDEX idx_order_applications_status ON bounty.order_applications USING btree (status);

CREATE INDEX idx_order_offers_order_id ON bounty.order_offers USING btree (order_id);
CREATE INDEX idx_order_offers_hunter_id ON bounty.order_offers USING btree (hunter_id);
CREATE INDEX idx_order_offers_status ON bounty.order_offers USING btree (status);
