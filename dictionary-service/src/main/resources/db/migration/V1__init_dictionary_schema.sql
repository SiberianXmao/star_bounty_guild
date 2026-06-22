CREATE SCHEMA IF NOT EXISTS bounty;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE bounty.faction_relation AS ENUM (
    'FRIENDLY',
    'NEUTRAL',
    'HOSTILE',
    'ALLIED'
);

CREATE TYPE bounty.faction_type AS ENUM (
    'GOVERNMENT',
    'CORPORATION',
    'GUILD',
    'PIRATES',
    'REBELS',
    'NEUTRAL'
);

CREATE TYPE bounty.planet_status AS ENUM (
    'ACTIVE',
    'RESTRICTED',
    'UNAVAILABLE'
);

CREATE TABLE bounty.currencies (
    code varchar(16) NOT NULL,
    name varchar(80) NOT NULL,
    symbol varchar(16),
    is_active boolean DEFAULT true NOT NULL,
    CONSTRAINT currencies_pkey PRIMARY KEY (code)
);

CREATE TABLE bounty.factions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name varchar(120) NOT NULL,
    description text,
    type bounty.faction_type DEFAULT 'NEUTRAL'::bounty.faction_type NOT NULL,
    influence_level integer DEFAULT 0 NOT NULL,
    relation_to_guild bounty.faction_relation DEFAULT 'NEUTRAL'::bounty.faction_relation NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT factions_pkey PRIMARY KEY (id),
    CONSTRAINT factions_name_key UNIQUE (name),
    CONSTRAINT factions_influence_level_check CHECK (influence_level >= 0 AND influence_level <= 100)
);

CREATE TABLE bounty.sectors (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    controlling_faction_id uuid,
    name varchar(120) NOT NULL,
    description text,
    stability_level integer DEFAULT 50 NOT NULL,
    danger_level integer DEFAULT 50 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT sectors_pkey PRIMARY KEY (id),
    CONSTRAINT sectors_name_key UNIQUE (name),
    CONSTRAINT sectors_stability_level_check CHECK (stability_level >= 0 AND stability_level <= 100),
    CONSTRAINT sectors_danger_level_check CHECK (danger_level >= 0 AND danger_level <= 100),
    CONSTRAINT sectors_controlling_faction_id_fkey
        FOREIGN KEY (controlling_faction_id) REFERENCES bounty.factions(id) ON DELETE SET NULL
);

CREATE TABLE bounty.planets (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    sector_id uuid NOT NULL,
    controlling_faction_id uuid,
    name varchar(120) NOT NULL,
    description text,
    danger_level integer DEFAULT 50 NOT NULL,
    development_level integer DEFAULT 50 NOT NULL,
    climate varchar(120),
    population bigint,
    status bounty.planet_status DEFAULT 'ACTIVE'::bounty.planet_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT planets_pkey PRIMARY KEY (id),
    CONSTRAINT planets_name_key UNIQUE (name),
    CONSTRAINT planets_danger_level_check CHECK (danger_level >= 0 AND danger_level <= 100),
    CONSTRAINT planets_development_level_check CHECK (development_level >= 0 AND development_level <= 100),
    CONSTRAINT planets_population_check CHECK (population IS NULL OR population >= 0),
    CONSTRAINT planets_sector_id_fkey
        FOREIGN KEY (sector_id) REFERENCES bounty.sectors(id) ON DELETE RESTRICT,
    CONSTRAINT planets_controlling_faction_id_fkey
        FOREIGN KEY (controlling_faction_id) REFERENCES bounty.factions(id) ON DELETE SET NULL
);

CREATE TABLE bounty.order_categories (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name varchar(120) NOT NULL,
    slug varchar(140) NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT order_categories_pkey PRIMARY KEY (id),
    CONSTRAINT order_categories_name_key UNIQUE (name),
    CONSTRAINT order_categories_slug_key UNIQUE (slug)
);

CREATE TABLE bounty.skills (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name varchar(120) NOT NULL,
    description text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT skills_pkey PRIMARY KEY (id),
    CONSTRAINT skills_name_key UNIQUE (name)
);

CREATE INDEX idx_planets_sector_id ON bounty.planets USING btree (sector_id);
CREATE INDEX idx_planets_controlling_faction_id ON bounty.planets USING btree (controlling_faction_id);
CREATE INDEX idx_sectors_controlling_faction_id ON bounty.sectors USING btree (controlling_faction_id);
