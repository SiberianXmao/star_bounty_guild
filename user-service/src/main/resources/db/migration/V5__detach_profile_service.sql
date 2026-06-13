ALTER TABLE IF EXISTS bounty.order_applications
    DROP CONSTRAINT IF EXISTS order_applications_hunter_id_fkey;

ALTER TABLE IF EXISTS bounty.order_offers
    DROP CONSTRAINT IF EXISTS order_offers_client_id_fkey,
    DROP CONSTRAINT IF EXISTS order_offers_hunter_id_fkey;

ALTER TABLE IF EXISTS bounty.orders
    DROP CONSTRAINT IF EXISTS orders_assigned_hunter_id_fkey,
    DROP CONSTRAINT IF EXISTS orders_client_id_fkey;

ALTER TABLE IF EXISTS bounty.hunter_skills
    RENAME TO legacy_hunter_skills;

ALTER TABLE IF EXISTS bounty.hunter_profiles
    RENAME TO legacy_hunter_profiles;

ALTER TABLE IF EXISTS bounty.client_profiles
    RENAME TO legacy_client_profiles;
