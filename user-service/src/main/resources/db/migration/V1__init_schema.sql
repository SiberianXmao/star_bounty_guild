--
-- PostgreSQL database dump
--

-- Dumped from database version 15.10
-- Dumped by pg_dump version 15.10

-- Started on 2026-05-26 11:24:40
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;

CREATE SCHEMA IF NOT EXISTS bounty;

SET search_path TO bounty, public;


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 8 (class 2615 OID 25070)
-- Name: bounty; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA IF NOT EXISTS bounty;


--
-- TOC entry 958 (class 1247 OID 25258)
-- Name: acceptance_mode; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.acceptance_mode AS ENUM (
    'APPLICATIONS',
    'INSTANT_ACCEPT',
    'PERSONAL_OFFER'
);



--
-- TOC entry 967 (class 1247 OID 25286)
-- Name: application_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.application_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'REJECTED',
    'WITHDRAWN'
);



--
-- TOC entry 949 (class 1247 OID 25222)
-- Name: availability_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.availability_status AS ENUM (
    'AVAILABLE',
    'BUSY',
    'UNAVAILABLE'
);



--
-- TOC entry 976 (class 1247 OID 25314)
-- Name: chat_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.chat_status AS ENUM (
    'ACTIVE',
    'ARCHIVED'
);



--
-- TOC entry 973 (class 1247 OID 25308)
-- Name: chat_type; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.chat_type AS ENUM (
    'ORDER_CHAT',
    'DIRECT_CHAT'
);



--
-- TOC entry 985 (class 1247 OID 25332)
-- Name: complaint_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.complaint_status AS ENUM (
    'OPEN',
    'IN_REVIEW',
    'RESOLVED',
    'REJECTED'
);



--
-- TOC entry 994 (class 1247 OID 25364)
-- Name: faction_relation; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.faction_relation AS ENUM (
    'FRIENDLY',
    'NEUTRAL',
    'HOSTILE',
    'ALLIED'
);



--
-- TOC entry 991 (class 1247 OID 25350)
-- Name: faction_type; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.faction_type AS ENUM (
    'GOVERNMENT',
    'CORPORATION',
    'GUILD',
    'PIRATES',
    'REBELS',
    'NEUTRAL'
);



--
-- TOC entry 982 (class 1247 OID 25326)
-- Name: favorite_type; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.favorite_type AS ENUM (
    'HUNTER',
    'CLIENT'
);



--
-- TOC entry 979 (class 1247 OID 25320)
-- Name: message_type; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.message_type AS ENUM (
    'USER',
    'SYSTEM'
);



--
-- TOC entry 970 (class 1247 OID 25296)
-- Name: offer_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.offer_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'DECLINED',
    'EXPIRED',
    'CANCELLED'
);



--
-- TOC entry 952 (class 1247 OID 25230)
-- Name: order_status; Type: TYPE; Schema: bounty; Owner: postgres
--

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



--
-- TOC entry 955 (class 1247 OID 25252)
-- Name: order_visibility; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.order_visibility AS ENUM (
    'PUBLIC',
    'PRIVATE'
);



--
-- TOC entry 988 (class 1247 OID 25342)
-- Name: planet_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.planet_status AS ENUM (
    'ACTIVE',
    'RESTRICTED',
    'UNAVAILABLE'
);



--
-- TOC entry 961 (class 1247 OID 25266)
-- Name: risk_level; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.risk_level AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'EXTREME'
);



--
-- TOC entry 964 (class 1247 OID 25276)
-- Name: urgency_level; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.urgency_level AS ENUM (
    'LOW',
    'NORMAL',
    'HIGH',
    'CRITICAL'
);



--
-- TOC entry 946 (class 1247 OID 25214)
-- Name: user_status; Type: TYPE; Schema: bounty; Owner: postgres
--

CREATE TYPE bounty.user_status AS ENUM (
    'ACTIVE',
    'BLOCKED',
    'DELETED'
);



SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 234 (class 1259 OID 25739)
-- Name: chat_messages; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.chat_messages (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    chat_id uuid NOT NULL,
    sender_id uuid,
    content text NOT NULL,
    message_type bounty.message_type DEFAULT 'USER'::bounty.message_type NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    edited_at timestamp with time zone,
    deleted_at timestamp with time zone,
    CONSTRAINT chat_messages_check CHECK (((message_type = 'SYSTEM'::bounty.message_type) OR ((message_type = 'USER'::bounty.message_type) AND (sender_id IS NOT NULL))))
);



--
-- TOC entry 233 (class 1259 OID 25723)
-- Name: chat_participants; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.chat_participants (
    chat_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role_in_chat character varying(50),
    joined_at timestamp with time zone DEFAULT now() NOT NULL,
    last_read_at timestamp with time zone
);



--
-- TOC entry 232 (class 1259 OID 25703)
-- Name: chats; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.chats (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    type bounty.chat_type NOT NULL,
    order_id uuid,
    direct_pair_key text,
    status bounty.chat_status DEFAULT 'ACTIVE'::bounty.chat_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT chats_check CHECK ((((type = 'ORDER_CHAT'::bounty.chat_type) AND (order_id IS NOT NULL) AND (direct_pair_key IS NULL)) OR ((type = 'DIRECT_CHAT'::bounty.chat_type) AND (order_id IS NULL) AND (direct_pair_key IS NOT NULL))))
);



--
-- TOC entry 226 (class 1259 OID 25512)
-- Name: client_profiles; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.client_profiles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    name character varying(120) NOT NULL,
    description text,
    faction_id uuid,
    planet_id uuid,
    reliability_score integer DEFAULT 50 NOT NULL,
    average_rating numeric(3,2) DEFAULT 0 NOT NULL,
    completed_orders_count integer DEFAULT 0 NOT NULL,
    cancelled_orders_count integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT client_profiles_average_rating_check CHECK (((average_rating >= (0)::numeric) AND (average_rating <= (5)::numeric))),
    CONSTRAINT client_profiles_cancelled_orders_count_check CHECK ((cancelled_orders_count >= 0)),
    CONSTRAINT client_profiles_completed_orders_count_check CHECK ((completed_orders_count >= 0)),
    CONSTRAINT client_profiles_reliability_score_check CHECK (((reliability_score >= 0) AND (reliability_score <= 100)))
);



--
-- TOC entry 238 (class 1259 OID 25834)
-- Name: complaints; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.complaints (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    author_id uuid NOT NULL,
    target_user_id uuid,
    order_id uuid,
    reason character varying(200) NOT NULL,
    description text,
    status bounty.complaint_status DEFAULT 'OPEN'::bounty.complaint_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    resolved_at timestamp with time zone
);



--
-- TOC entry 221 (class 1259 OID 25453)
-- Name: currencies; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.currencies (
    code character varying(16) NOT NULL,
    name character varying(80) NOT NULL,
    symbol character varying(16),
    is_active boolean DEFAULT true NOT NULL
);



--
-- TOC entry 217 (class 1259 OID 25373)
-- Name: factions; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.factions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(120) NOT NULL,
    description text,
    type bounty.faction_type DEFAULT 'NEUTRAL'::bounty.faction_type NOT NULL,
    influence_level integer DEFAULT 0 NOT NULL,
    relation_to_guild bounty.faction_relation DEFAULT 'NEUTRAL'::bounty.faction_relation NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT factions_influence_level_check CHECK (((influence_level >= 0) AND (influence_level <= 100)))
);



--
-- TOC entry 236 (class 1259 OID 25789)
-- Name: favorites; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.favorites (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    target_user_id uuid NOT NULL,
    type bounty.favorite_type NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT favorites_check CHECK ((user_id <> target_user_id))
);



--
-- TOC entry 239 (class 1259 OID 25890)
-- Name: flyway_schema_history; Type: TABLE; Schema: bounty; Owner: postgres
--




--
-- TOC entry 227 (class 1259 OID 25547)
-- Name: hunter_profiles; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.hunter_profiles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    callsign character varying(120) NOT NULL,
    bio text,
    faction_id uuid,
    home_planet_id uuid,
    availability_status bounty.availability_status DEFAULT 'AVAILABLE'::bounty.availability_status NOT NULL,
    min_reward numeric(14,2),
    reliability_score integer DEFAULT 50 NOT NULL,
    average_rating numeric(3,2) DEFAULT 0 NOT NULL,
    completed_orders_count integer DEFAULT 0 NOT NULL,
    failed_orders_count integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT hunter_profiles_average_rating_check CHECK (((average_rating >= (0)::numeric) AND (average_rating <= (5)::numeric))),
    CONSTRAINT hunter_profiles_completed_orders_count_check CHECK ((completed_orders_count >= 0)),
    CONSTRAINT hunter_profiles_failed_orders_count_check CHECK ((failed_orders_count >= 0)),
    CONSTRAINT hunter_profiles_min_reward_check CHECK (((min_reward IS NULL) OR (min_reward >= (0)::numeric))),
    CONSTRAINT hunter_profiles_reliability_score_check CHECK (((reliability_score >= 0) AND (reliability_score <= 100)))
);



--
-- TOC entry 228 (class 1259 OID 25586)
-- Name: hunter_skills; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.hunter_skills (
    hunter_profile_id uuid NOT NULL,
    skill_id uuid NOT NULL,
    level integer DEFAULT 1 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT hunter_skills_level_check CHECK (((level >= 1) AND (level <= 100)))
);



--
-- TOC entry 237 (class 1259 OID 25809)
-- Name: notifications; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.notifications (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    type character varying(80) NOT NULL,
    title character varying(200) NOT NULL,
    message text,
    is_read boolean DEFAULT false NOT NULL,
    related_order_id uuid,
    related_chat_id uuid,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);



--
-- TOC entry 230 (class 1259 OID 25651)
-- Name: order_applications; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.order_applications (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    hunter_id uuid NOT NULL,
    message text,
    proposed_reward numeric(14,2),
    status bounty.application_status DEFAULT 'PENDING'::bounty.application_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT order_applications_proposed_reward_check CHECK (((proposed_reward IS NULL) OR (proposed_reward >= (0)::numeric)))
);



--
-- TOC entry 220 (class 1259 OID 25438)
-- Name: order_categories; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.order_categories (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(120) NOT NULL,
    slug character varying(140) NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);



--
-- TOC entry 231 (class 1259 OID 25675)
-- Name: order_offers; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.order_offers (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    client_id uuid NOT NULL,
    hunter_id uuid NOT NULL,
    message text,
    status bounty.offer_status DEFAULT 'PENDING'::bounty.offer_status NOT NULL,
    expires_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);



--
-- TOC entry 229 (class 1259 OID 25604)
-- Name: orders; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.orders (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    client_id uuid NOT NULL,
    assigned_hunter_id uuid,
    title character varying(200) NOT NULL,
    description text NOT NULL,
    category_id uuid,
    reward_amount numeric(14,2) NOT NULL,
    reward_currency_code character varying(16) NOT NULL,
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
    CONSTRAINT orders_check CHECK (((status <> 'COMPLETED'::bounty.order_status) OR (completed_at IS NOT NULL))),
    CONSTRAINT orders_reward_amount_check CHECK ((reward_amount >= (0)::numeric))
);



--
-- TOC entry 219 (class 1259 OID 25410)
-- Name: planets; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.planets (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    sector_id uuid,
    controlling_faction_id uuid,
    name character varying(120) NOT NULL,
    description text,
    danger_level integer DEFAULT 50 NOT NULL,
    development_level integer DEFAULT 50 NOT NULL,
    climate character varying(120),
    population bigint,
    status bounty.planet_status DEFAULT 'ACTIVE'::bounty.planet_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT planets_danger_level_check CHECK (((danger_level >= 0) AND (danger_level <= 100))),
    CONSTRAINT planets_development_level_check CHECK (((development_level >= 0) AND (development_level <= 100))),
    CONSTRAINT planets_population_check CHECK (((population IS NULL) OR (population >= 0)))
);



--
-- TOC entry 240 (class 1259 OID 25913)
-- Name: refresh_tokens; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.refresh_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    token_hash character varying(128) NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    revoked_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);



--
-- TOC entry 235 (class 1259 OID 25760)
-- Name: reviews; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.reviews (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    author_id uuid NOT NULL,
    target_user_id uuid NOT NULL,
    rating integer NOT NULL,
    text text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT reviews_check CHECK ((author_id <> target_user_id)),
    CONSTRAINT reviews_rating_check CHECK (((rating >= 1) AND (rating <= 5)))
);



--
-- TOC entry 224 (class 1259 OID 25486)
-- Name: roles; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.roles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(50) NOT NULL,
    description text
);



--
-- TOC entry 218 (class 1259 OID 25389)
-- Name: sectors; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.sectors (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    controlling_faction_id uuid,
    name character varying(120) NOT NULL,
    description text,
    stability_level integer DEFAULT 50 NOT NULL,
    danger_level integer DEFAULT 50 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT sectors_danger_level_check CHECK (((danger_level >= 0) AND (danger_level <= 100))),
    CONSTRAINT sectors_stability_level_check CHECK (((stability_level >= 0) AND (stability_level <= 100)))
);



--
-- TOC entry 222 (class 1259 OID 25459)
-- Name: skills; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.skills (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(120) NOT NULL,
    description text,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);



--
-- TOC entry 225 (class 1259 OID 25496)
-- Name: user_roles; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.user_roles (
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);



--
-- TOC entry 223 (class 1259 OID 25470)
-- Name: users; Type: TABLE; Schema: bounty; Owner: postgres
--

CREATE TABLE bounty.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    email character varying(255) NOT NULL,
    password_hash text NOT NULL,
    username character varying(80) NOT NULL,
    display_name character varying(120),
    avatar_url text,
    status bounty.user_status DEFAULT 'ACTIVE'::bounty.user_status NOT NULL,
    email_verified boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    last_login_at timestamp with time zone
);



--
-- TOC entry 3648 (class 2606 OID 25749)
-- Name: chat_messages chat_messages_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chat_messages
    ADD CONSTRAINT chat_messages_pkey PRIMARY KEY (id);


--
-- TOC entry 3646 (class 2606 OID 25728)
-- Name: chat_participants chat_participants_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chat_participants
    ADD CONSTRAINT chat_participants_pkey PRIMARY KEY (chat_id, user_id);


--
-- TOC entry 3641 (class 2606 OID 25716)
-- Name: chats chats_direct_pair_key_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chats
    ADD CONSTRAINT chats_direct_pair_key_key UNIQUE (direct_pair_key);


--
-- TOC entry 3643 (class 2606 OID 25714)
-- Name: chats chats_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chats
    ADD CONSTRAINT chats_pkey PRIMARY KEY (id);


--
-- TOC entry 3594 (class 2606 OID 25529)
-- Name: client_profiles client_profiles_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.client_profiles
    ADD CONSTRAINT client_profiles_pkey PRIMARY KEY (id);


--
-- TOC entry 3596 (class 2606 OID 25531)
-- Name: client_profiles client_profiles_user_id_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.client_profiles
    ADD CONSTRAINT client_profiles_user_id_key UNIQUE (user_id);


--
-- TOC entry 3663 (class 2606 OID 25843)
-- Name: complaints complaints_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.complaints
    ADD CONSTRAINT complaints_pkey PRIMARY KEY (id);


--
-- TOC entry 3572 (class 2606 OID 25458)
-- Name: currencies currencies_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.currencies
    ADD CONSTRAINT currencies_pkey PRIMARY KEY (code);


--
-- TOC entry 3554 (class 2606 OID 25388)
-- Name: factions factions_name_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.factions
    ADD CONSTRAINT factions_name_key UNIQUE (name);


--
-- TOC entry 3556 (class 2606 OID 25386)
-- Name: factions factions_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.factions
    ADD CONSTRAINT factions_pkey PRIMARY KEY (id);


--
-- TOC entry 3655 (class 2606 OID 25796)
-- Name: favorites favorites_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.favorites
    ADD CONSTRAINT favorites_pkey PRIMARY KEY (id);


--
-- TOC entry 3657 (class 2606 OID 25798)
-- Name: favorites favorites_user_id_target_user_id_type_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.favorites
    ADD CONSTRAINT favorites_user_id_target_user_id_type_key UNIQUE (user_id, target_user_id, type);


--
-- TOC entry 3666 (class 2606 OID 25897)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--


--
-- TOC entry 3600 (class 2606 OID 25570)
-- Name: hunter_profiles hunter_profiles_callsign_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_callsign_key UNIQUE (callsign);


--
-- TOC entry 3602 (class 2606 OID 25566)
-- Name: hunter_profiles hunter_profiles_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_pkey PRIMARY KEY (id);


--
-- TOC entry 3604 (class 2606 OID 25568)
-- Name: hunter_profiles hunter_profiles_user_id_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_user_id_key UNIQUE (user_id);


--
-- TOC entry 3611 (class 2606 OID 25593)
-- Name: hunter_skills hunter_skills_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_skills
    ADD CONSTRAINT hunter_skills_pkey PRIMARY KEY (hunter_profile_id, skill_id);


--
-- TOC entry 3661 (class 2606 OID 25818)
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 3630 (class 2606 OID 25664)
-- Name: order_applications order_applications_order_id_hunter_id_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_applications
    ADD CONSTRAINT order_applications_order_id_hunter_id_key UNIQUE (order_id, hunter_id);


--
-- TOC entry 3632 (class 2606 OID 25662)
-- Name: order_applications order_applications_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_applications
    ADD CONSTRAINT order_applications_pkey PRIMARY KEY (id);


--
-- TOC entry 3566 (class 2606 OID 25450)
-- Name: order_categories order_categories_name_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_categories
    ADD CONSTRAINT order_categories_name_key UNIQUE (name);


--
-- TOC entry 3568 (class 2606 OID 25448)
-- Name: order_categories order_categories_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_categories
    ADD CONSTRAINT order_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 3570 (class 2606 OID 25452)
-- Name: order_categories order_categories_slug_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_categories
    ADD CONSTRAINT order_categories_slug_key UNIQUE (slug);


--
-- TOC entry 3637 (class 2606 OID 25687)
-- Name: order_offers order_offers_order_id_hunter_id_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_offers
    ADD CONSTRAINT order_offers_order_id_hunter_id_key UNIQUE (order_id, hunter_id);


--
-- TOC entry 3639 (class 2606 OID 25685)
-- Name: order_offers order_offers_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_offers
    ADD CONSTRAINT order_offers_pkey PRIMARY KEY (id);


--
-- TOC entry 3625 (class 2606 OID 25620)
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3562 (class 2606 OID 25427)
-- Name: planets planets_name_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.planets
    ADD CONSTRAINT planets_name_key UNIQUE (name);


--
-- TOC entry 3564 (class 2606 OID 25425)
-- Name: planets planets_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.planets
    ADD CONSTRAINT planets_pkey PRIMARY KEY (id);


--
-- TOC entry 3669 (class 2606 OID 25919)
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 3671 (class 2606 OID 25921)
-- Name: refresh_tokens refresh_tokens_token_hash_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_hash_key UNIQUE (token_hash);


--
-- TOC entry 3651 (class 2606 OID 25773)
-- Name: reviews reviews_order_id_author_id_target_user_id_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.reviews
    ADD CONSTRAINT reviews_order_id_author_id_target_user_id_key UNIQUE (order_id, author_id, target_user_id);


--
-- TOC entry 3653 (class 2606 OID 25771)
-- Name: reviews reviews_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.reviews
    ADD CONSTRAINT reviews_pkey PRIMARY KEY (id);


--
-- TOC entry 3588 (class 2606 OID 25495)
-- Name: roles roles_name_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- TOC entry 3590 (class 2606 OID 25493)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 3558 (class 2606 OID 25404)
-- Name: sectors sectors_name_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.sectors
    ADD CONSTRAINT sectors_name_key UNIQUE (name);


--
-- TOC entry 3560 (class 2606 OID 25402)
-- Name: sectors sectors_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.sectors
    ADD CONSTRAINT sectors_pkey PRIMARY KEY (id);


--
-- TOC entry 3574 (class 2606 OID 25469)
-- Name: skills skills_name_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.skills
    ADD CONSTRAINT skills_name_key UNIQUE (name);


--
-- TOC entry 3576 (class 2606 OID 25467)
-- Name: skills skills_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.skills
    ADD CONSTRAINT skills_pkey PRIMARY KEY (id);


--
-- TOC entry 3592 (class 2606 OID 25501)
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- TOC entry 3580 (class 2606 OID 25900)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 3582 (class 2606 OID 25481)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3584 (class 2606 OID 25485)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 3667 (class 1259 OID 25898)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: bounty; Owner: postgres
--



--
-- TOC entry 3649 (class 1259 OID 25885)
-- Name: idx_chat_messages_chat_created; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_chat_messages_chat_created ON bounty.chat_messages USING btree (chat_id, created_at);


--
-- TOC entry 3597 (class 1259 OID 25861)
-- Name: idx_client_profiles_rating; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_client_profiles_rating ON bounty.client_profiles USING btree (average_rating);


--
-- TOC entry 3598 (class 1259 OID 25862)
-- Name: idx_client_profiles_reliability; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_client_profiles_reliability ON bounty.client_profiles USING btree (reliability_score);


--
-- TOC entry 3664 (class 1259 OID 25888)
-- Name: idx_complaints_status; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_complaints_status ON bounty.complaints USING btree (status);


--
-- TOC entry 3605 (class 1259 OID 25865)
-- Name: idx_hunter_profiles_availability; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_hunter_profiles_availability ON bounty.hunter_profiles USING btree (availability_status);


--
-- TOC entry 3606 (class 1259 OID 25866)
-- Name: idx_hunter_profiles_min_reward; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_hunter_profiles_min_reward ON bounty.hunter_profiles USING btree (min_reward);


--
-- TOC entry 3607 (class 1259 OID 25863)
-- Name: idx_hunter_profiles_rating; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_hunter_profiles_rating ON bounty.hunter_profiles USING btree (average_rating);


--
-- TOC entry 3608 (class 1259 OID 25864)
-- Name: idx_hunter_profiles_reliability; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_hunter_profiles_reliability ON bounty.hunter_profiles USING btree (reliability_score);


--
-- TOC entry 3658 (class 1259 OID 25887)
-- Name: idx_notifications_user_created; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_notifications_user_created ON bounty.notifications USING btree (user_id, created_at DESC);


--
-- TOC entry 3659 (class 1259 OID 25886)
-- Name: idx_notifications_user_read; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_notifications_user_read ON bounty.notifications USING btree (user_id, is_read);


--
-- TOC entry 3626 (class 1259 OID 25880)
-- Name: idx_order_applications_hunter_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_order_applications_hunter_id ON bounty.order_applications USING btree (hunter_id);


--
-- TOC entry 3627 (class 1259 OID 25879)
-- Name: idx_order_applications_order_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_order_applications_order_id ON bounty.order_applications USING btree (order_id);


--
-- TOC entry 3628 (class 1259 OID 25881)
-- Name: idx_order_applications_status; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_order_applications_status ON bounty.order_applications USING btree (status);


--
-- TOC entry 3633 (class 1259 OID 25883)
-- Name: idx_order_offers_hunter_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_order_offers_hunter_id ON bounty.order_offers USING btree (hunter_id);


--
-- TOC entry 3634 (class 1259 OID 25882)
-- Name: idx_order_offers_order_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_order_offers_order_id ON bounty.order_offers USING btree (order_id);


--
-- TOC entry 3635 (class 1259 OID 25884)
-- Name: idx_order_offers_status; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_order_offers_status ON bounty.order_offers USING btree (status);


--
-- TOC entry 3612 (class 1259 OID 25869)
-- Name: idx_orders_assigned_hunter_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_assigned_hunter_id ON bounty.orders USING btree (assigned_hunter_id);


--
-- TOC entry 3613 (class 1259 OID 25870)
-- Name: idx_orders_category_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_category_id ON bounty.orders USING btree (category_id);


--
-- TOC entry 3614 (class 1259 OID 25868)
-- Name: idx_orders_client_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_client_id ON bounty.orders USING btree (client_id);


--
-- TOC entry 3615 (class 1259 OID 25877)
-- Name: idx_orders_deadline; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_deadline ON bounty.orders USING btree (deadline);


--
-- TOC entry 3616 (class 1259 OID 25871)
-- Name: idx_orders_planet_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_planet_id ON bounty.orders USING btree (planet_id);


--
-- TOC entry 3617 (class 1259 OID 25876)
-- Name: idx_orders_published_at; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_published_at ON bounty.orders USING btree (published_at);


--
-- TOC entry 3618 (class 1259 OID 25875)
-- Name: idx_orders_reward_amount; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_reward_amount ON bounty.orders USING btree (reward_amount);


--
-- TOC entry 3619 (class 1259 OID 25873)
-- Name: idx_orders_risk_level; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_risk_level ON bounty.orders USING btree (risk_level);


--
-- TOC entry 3620 (class 1259 OID 25878)
-- Name: idx_orders_search; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_search ON bounty.orders USING gin (to_tsvector('simple'::regconfig, (((COALESCE(title, ''::character varying))::text || ' '::text) || COALESCE(description, ''::text))));


--
-- TOC entry 3621 (class 1259 OID 25872)
-- Name: idx_orders_sector_id; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_sector_id ON bounty.orders USING btree (sector_id);


--
-- TOC entry 3622 (class 1259 OID 25867)
-- Name: idx_orders_status; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_status ON bounty.orders USING btree (status);


--
-- TOC entry 3623 (class 1259 OID 25874)
-- Name: idx_orders_urgency_level; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_orders_urgency_level ON bounty.orders USING btree (urgency_level);


--
-- TOC entry 3577 (class 1259 OID 25860)
-- Name: idx_users_created_at; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_users_created_at ON bounty.users USING btree (created_at);


--
-- TOC entry 3578 (class 1259 OID 25859)
-- Name: idx_users_status; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE INDEX idx_users_status ON bounty.users USING btree (status);


--
-- TOC entry 3644 (class 1259 OID 25722)
-- Name: uq_order_chat_per_order; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE UNIQUE INDEX uq_order_chat_per_order ON bounty.chats USING btree (order_id) WHERE (type = 'ORDER_CHAT'::bounty.chat_type);


--
-- TOC entry 3609 (class 1259 OID 25927)
-- Name: ux_hunter_profiles_callsign_lower; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE UNIQUE INDEX ux_hunter_profiles_callsign_lower ON bounty.hunter_profiles USING btree (lower((callsign)::text));


--
-- TOC entry 3585 (class 1259 OID 25911)
-- Name: ux_users_email_lower; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE UNIQUE INDEX ux_users_email_lower ON bounty.users USING btree (lower((email)::text));


--
-- TOC entry 3586 (class 1259 OID 25912)
-- Name: ux_users_username_lower; Type: INDEX; Schema: bounty; Owner: postgres
--

CREATE UNIQUE INDEX ux_users_username_lower ON bounty.users USING btree (lower((username)::text));


--
-- TOC entry 3699 (class 2606 OID 25750)
-- Name: chat_messages chat_messages_chat_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chat_messages
    ADD CONSTRAINT chat_messages_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES bounty.chats(id) ON DELETE CASCADE;


--
-- TOC entry 3700 (class 2606 OID 25755)
-- Name: chat_messages chat_messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chat_messages
    ADD CONSTRAINT chat_messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES bounty.users(id) ON DELETE SET NULL;


--
-- TOC entry 3697 (class 2606 OID 25729)
-- Name: chat_participants chat_participants_chat_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chat_participants
    ADD CONSTRAINT chat_participants_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES bounty.chats(id) ON DELETE CASCADE;


--
-- TOC entry 3698 (class 2606 OID 25734)
-- Name: chat_participants chat_participants_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chat_participants
    ADD CONSTRAINT chat_participants_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3696 (class 2606 OID 25717)
-- Name: chats chats_order_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.chats
    ADD CONSTRAINT chats_order_id_fkey FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE;


--
-- TOC entry 3677 (class 2606 OID 25537)
-- Name: client_profiles client_profiles_faction_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.client_profiles
    ADD CONSTRAINT client_profiles_faction_id_fkey FOREIGN KEY (faction_id) REFERENCES bounty.factions(id) ON DELETE SET NULL;


--
-- TOC entry 3678 (class 2606 OID 25542)
-- Name: client_profiles client_profiles_planet_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.client_profiles
    ADD CONSTRAINT client_profiles_planet_id_fkey FOREIGN KEY (planet_id) REFERENCES bounty.planets(id) ON DELETE SET NULL;


--
-- TOC entry 3679 (class 2606 OID 25532)
-- Name: client_profiles client_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.client_profiles
    ADD CONSTRAINT client_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3709 (class 2606 OID 25844)
-- Name: complaints complaints_author_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.complaints
    ADD CONSTRAINT complaints_author_id_fkey FOREIGN KEY (author_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3710 (class 2606 OID 25854)
-- Name: complaints complaints_order_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.complaints
    ADD CONSTRAINT complaints_order_id_fkey FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE SET NULL;


--
-- TOC entry 3711 (class 2606 OID 25849)
-- Name: complaints complaints_target_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.complaints
    ADD CONSTRAINT complaints_target_user_id_fkey FOREIGN KEY (target_user_id) REFERENCES bounty.users(id) ON DELETE SET NULL;


--
-- TOC entry 3704 (class 2606 OID 25804)
-- Name: favorites favorites_target_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.favorites
    ADD CONSTRAINT favorites_target_user_id_fkey FOREIGN KEY (target_user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3705 (class 2606 OID 25799)
-- Name: favorites favorites_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.favorites
    ADD CONSTRAINT favorites_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3680 (class 2606 OID 25576)
-- Name: hunter_profiles hunter_profiles_faction_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_faction_id_fkey FOREIGN KEY (faction_id) REFERENCES bounty.factions(id) ON DELETE SET NULL;


--
-- TOC entry 3681 (class 2606 OID 25581)
-- Name: hunter_profiles hunter_profiles_home_planet_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_home_planet_id_fkey FOREIGN KEY (home_planet_id) REFERENCES bounty.planets(id) ON DELETE SET NULL;


--
-- TOC entry 3682 (class 2606 OID 25571)
-- Name: hunter_profiles hunter_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_profiles
    ADD CONSTRAINT hunter_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3683 (class 2606 OID 25594)
-- Name: hunter_skills hunter_skills_hunter_profile_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_skills
    ADD CONSTRAINT hunter_skills_hunter_profile_id_fkey FOREIGN KEY (hunter_profile_id) REFERENCES bounty.hunter_profiles(id) ON DELETE CASCADE;


--
-- TOC entry 3684 (class 2606 OID 25599)
-- Name: hunter_skills hunter_skills_skill_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.hunter_skills
    ADD CONSTRAINT hunter_skills_skill_id_fkey FOREIGN KEY (skill_id) REFERENCES bounty.skills(id) ON DELETE CASCADE;


--
-- TOC entry 3706 (class 2606 OID 25829)
-- Name: notifications notifications_related_chat_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.notifications
    ADD CONSTRAINT notifications_related_chat_id_fkey FOREIGN KEY (related_chat_id) REFERENCES bounty.chats(id) ON DELETE SET NULL;


--
-- TOC entry 3707 (class 2606 OID 25824)
-- Name: notifications notifications_related_order_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.notifications
    ADD CONSTRAINT notifications_related_order_id_fkey FOREIGN KEY (related_order_id) REFERENCES bounty.orders(id) ON DELETE SET NULL;


--
-- TOC entry 3708 (class 2606 OID 25819)
-- Name: notifications notifications_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3691 (class 2606 OID 25670)
-- Name: order_applications order_applications_hunter_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_applications
    ADD CONSTRAINT order_applications_hunter_id_fkey FOREIGN KEY (hunter_id) REFERENCES bounty.hunter_profiles(id) ON DELETE CASCADE;


--
-- TOC entry 3692 (class 2606 OID 25665)
-- Name: order_applications order_applications_order_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_applications
    ADD CONSTRAINT order_applications_order_id_fkey FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE;


--
-- TOC entry 3693 (class 2606 OID 25693)
-- Name: order_offers order_offers_client_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_offers
    ADD CONSTRAINT order_offers_client_id_fkey FOREIGN KEY (client_id) REFERENCES bounty.client_profiles(id) ON DELETE CASCADE;


--
-- TOC entry 3694 (class 2606 OID 25698)
-- Name: order_offers order_offers_hunter_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_offers
    ADD CONSTRAINT order_offers_hunter_id_fkey FOREIGN KEY (hunter_id) REFERENCES bounty.hunter_profiles(id) ON DELETE CASCADE;


--
-- TOC entry 3695 (class 2606 OID 25688)
-- Name: order_offers order_offers_order_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.order_offers
    ADD CONSTRAINT order_offers_order_id_fkey FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE;


--
-- TOC entry 3685 (class 2606 OID 25626)
-- Name: orders orders_assigned_hunter_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_assigned_hunter_id_fkey FOREIGN KEY (assigned_hunter_id) REFERENCES bounty.hunter_profiles(id) ON DELETE SET NULL;


--
-- TOC entry 3686 (class 2606 OID 25631)
-- Name: orders orders_category_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_category_id_fkey FOREIGN KEY (category_id) REFERENCES bounty.order_categories(id) ON DELETE SET NULL;


--
-- TOC entry 3687 (class 2606 OID 25621)
-- Name: orders orders_client_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_client_id_fkey FOREIGN KEY (client_id) REFERENCES bounty.client_profiles(id) ON DELETE RESTRICT;


--
-- TOC entry 3688 (class 2606 OID 25641)
-- Name: orders orders_planet_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_planet_id_fkey FOREIGN KEY (planet_id) REFERENCES bounty.planets(id) ON DELETE SET NULL;


--
-- TOC entry 3689 (class 2606 OID 25636)
-- Name: orders orders_reward_currency_code_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_reward_currency_code_fkey FOREIGN KEY (reward_currency_code) REFERENCES bounty.currencies(code) ON DELETE RESTRICT;


--
-- TOC entry 3690 (class 2606 OID 25646)
-- Name: orders orders_sector_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.orders
    ADD CONSTRAINT orders_sector_id_fkey FOREIGN KEY (sector_id) REFERENCES bounty.sectors(id) ON DELETE SET NULL;


--
-- TOC entry 3673 (class 2606 OID 25433)
-- Name: planets planets_controlling_faction_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.planets
    ADD CONSTRAINT planets_controlling_faction_id_fkey FOREIGN KEY (controlling_faction_id) REFERENCES bounty.factions(id) ON DELETE SET NULL;


--
-- TOC entry 3674 (class 2606 OID 25428)
-- Name: planets planets_sector_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.planets
    ADD CONSTRAINT planets_sector_id_fkey FOREIGN KEY (sector_id) REFERENCES bounty.sectors(id) ON DELETE SET NULL;


--
-- TOC entry 3712 (class 2606 OID 25922)
-- Name: refresh_tokens refresh_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.refresh_tokens
    ADD CONSTRAINT refresh_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3701 (class 2606 OID 25779)
-- Name: reviews reviews_author_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.reviews
    ADD CONSTRAINT reviews_author_id_fkey FOREIGN KEY (author_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3702 (class 2606 OID 25774)
-- Name: reviews reviews_order_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.reviews
    ADD CONSTRAINT reviews_order_id_fkey FOREIGN KEY (order_id) REFERENCES bounty.orders(id) ON DELETE CASCADE;


--
-- TOC entry 3703 (class 2606 OID 25784)
-- Name: reviews reviews_target_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.reviews
    ADD CONSTRAINT reviews_target_user_id_fkey FOREIGN KEY (target_user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


--
-- TOC entry 3672 (class 2606 OID 25405)
-- Name: sectors sectors_controlling_faction_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.sectors
    ADD CONSTRAINT sectors_controlling_faction_id_fkey FOREIGN KEY (controlling_faction_id) REFERENCES bounty.factions(id) ON DELETE SET NULL;


--
-- TOC entry 3675 (class 2606 OID 25507)
-- Name: user_roles user_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.user_roles
    ADD CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES bounty.roles(id) ON DELETE CASCADE;


--
-- TOC entry 3676 (class 2606 OID 25502)
-- Name: user_roles user_roles_user_id_fkey; Type: FK CONSTRAINT; Schema: bounty; Owner: postgres
--

ALTER TABLE ONLY bounty.user_roles
    ADD CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES bounty.users(id) ON DELETE CASCADE;


-- Completed on 2026-05-26 11:24:41

--
-- PostgreSQL database dump complete
--

