ALTER TABLE IF EXISTS bounty.client_profiles
    DROP CONSTRAINT IF EXISTS client_profiles_faction_id_fkey;

ALTER TABLE IF EXISTS bounty.client_profiles
    DROP CONSTRAINT IF EXISTS client_profiles_planet_id_fkey;

ALTER TABLE IF EXISTS bounty.hunter_profiles
    DROP CONSTRAINT IF EXISTS hunter_profiles_faction_id_fkey;

ALTER TABLE IF EXISTS bounty.hunter_profiles
    DROP CONSTRAINT IF EXISTS hunter_profiles_home_planet_id_fkey;

ALTER TABLE IF EXISTS bounty.hunter_skills
    DROP CONSTRAINT IF EXISTS hunter_skills_skill_id_fkey;

ALTER TABLE IF EXISTS bounty.orders
    DROP CONSTRAINT IF EXISTS orders_category_id_fkey;

ALTER TABLE IF EXISTS bounty.orders
    DROP CONSTRAINT IF EXISTS orders_planet_id_fkey;

ALTER TABLE IF EXISTS bounty.orders
    DROP CONSTRAINT IF EXISTS orders_reward_currency_code_fkey;

ALTER TABLE IF EXISTS bounty.orders
    DROP CONSTRAINT IF EXISTS orders_sector_id_fkey;

DROP TABLE IF EXISTS bounty.planets CASCADE;
DROP TABLE IF EXISTS bounty.sectors CASCADE;
DROP TABLE IF EXISTS bounty.factions CASCADE;
DROP TABLE IF EXISTS bounty.currencies CASCADE;
DROP TABLE IF EXISTS bounty.order_categories CASCADE;
DROP TABLE IF EXISTS bounty.skills CASCADE;

DROP TYPE IF EXISTS bounty.planet_status;
DROP TYPE IF EXISTS bounty.faction_relation;
DROP TYPE IF EXISTS bounty.faction_type;
