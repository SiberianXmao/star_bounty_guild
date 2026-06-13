CREATE SCHEMA IF NOT EXISTS bounty;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE bounty.availability_status AS ENUM (
    'AVAILABLE',
    'BUSY',
    'UNAVAILABLE'
);

CREATE TABLE bounty.client_profiles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    name varchar(120) NOT NULL,
    description text,
    faction_id uuid,
    planet_id uuid,
    reliability_score integer DEFAULT 50 NOT NULL,
    average_rating numeric(3, 2) DEFAULT 0 NOT NULL,
    completed_orders_count integer DEFAULT 0 NOT NULL,
    cancelled_orders_count integer DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT client_profiles_pkey PRIMARY KEY (id),
    CONSTRAINT client_profiles_user_id_key UNIQUE (user_id),
    CONSTRAINT client_profiles_average_rating_check CHECK (average_rating >= 0 AND average_rating <= 5),
    CONSTRAINT client_profiles_cancelled_orders_count_check CHECK (cancelled_orders_count >= 0),
    CONSTRAINT client_profiles_completed_orders_count_check CHECK (completed_orders_count >= 0),
    CONSTRAINT client_profiles_reliability_score_check CHECK (reliability_score >= 0 AND reliability_score <= 100)
);

CREATE TABLE bounty.hunter_profiles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    callsign varchar(120) NOT NULL,
    bio text,
    faction_id uuid,
    home_planet_id uuid,
    availability_status bounty.availability_status DEFAULT 'AVAILABLE'::bounty.availability_status NOT NULL,
    min_reward numeric(14, 2),
    reliability_score integer DEFAULT 50 NOT NULL,
    average_rating numeric(3, 2) DEFAULT 0 NOT NULL,
    completed_orders_count integer DEFAULT 0 NOT NULL,
    failed_orders_count integer DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT hunter_profiles_pkey PRIMARY KEY (id),
    CONSTRAINT hunter_profiles_user_id_key UNIQUE (user_id),
    CONSTRAINT hunter_profiles_callsign_key UNIQUE (callsign),
    CONSTRAINT hunter_profiles_average_rating_check CHECK (average_rating >= 0 AND average_rating <= 5),
    CONSTRAINT hunter_profiles_completed_orders_count_check CHECK (completed_orders_count >= 0),
    CONSTRAINT hunter_profiles_failed_orders_count_check CHECK (failed_orders_count >= 0),
    CONSTRAINT hunter_profiles_min_reward_check CHECK (min_reward IS NULL OR min_reward >= 0),
    CONSTRAINT hunter_profiles_reliability_score_check CHECK (reliability_score >= 0 AND reliability_score <= 100)
);

CREATE TABLE bounty.hunter_skills (
    hunter_profile_id uuid NOT NULL,
    skill_id uuid NOT NULL,
    level integer DEFAULT 1 NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT hunter_skills_pkey PRIMARY KEY (hunter_profile_id, skill_id),
    CONSTRAINT hunter_skills_level_check CHECK (level >= 1 AND level <= 100),
    CONSTRAINT hunter_skills_hunter_profile_id_fkey
        FOREIGN KEY (hunter_profile_id) REFERENCES bounty.hunter_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_client_profiles_rating ON bounty.client_profiles (average_rating);
CREATE INDEX idx_client_profiles_reliability ON bounty.client_profiles (reliability_score);
CREATE INDEX idx_hunter_profiles_availability ON bounty.hunter_profiles (availability_status);
CREATE INDEX idx_hunter_profiles_min_reward ON bounty.hunter_profiles (min_reward);
CREATE INDEX idx_hunter_profiles_rating ON bounty.hunter_profiles (average_rating);
CREATE INDEX idx_hunter_profiles_reliability ON bounty.hunter_profiles (reliability_score);
