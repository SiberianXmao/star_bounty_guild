UPDATE bounty.client_profiles
SET
    name = 'Mos Espa Guild Desk',
    description = 'A discreet Tatooine contract desk serving moisture clans, dock crews and debt brokers near Mos Espa.',
    faction_id = '40000000-0000-0000-0000-000000000004',
    planet_id = '60000000-0000-0000-0000-000000000002',
    reliability_score = 68,
    average_rating = 4.30,
    completed_orders_count = 12,
    cancelled_orders_count = 1,
    updated_at = now()
WHERE id = '71000000-0000-0000-0000-000000000001';

UPDATE bounty.client_profiles
SET
    name = 'Lothal Relief Convoy',
    description = 'Frontier logistics group moving medicine, machine parts and settlement supplies across exposed Outer Rim lanes.',
    faction_id = '40000000-0000-0000-0000-000000000006',
    planet_id = '60000000-0000-0000-0000-000000000005',
    reliability_score = 84,
    average_rating = 4.70,
    completed_orders_count = 18,
    cancelled_orders_count = 0,
    updated_at = now()
WHERE id = '71000000-0000-0000-0000-000000000002';

UPDATE bounty.hunter_profiles
SET
    callsign = 'Beskar Raven',
    bio = 'Independent armored hunter known for desert tracking, fugitive recovery and hard extraction work in Hutt Space.',
    faction_id = '40000000-0000-0000-0000-000000000001',
    home_planet_id = '60000000-0000-0000-0000-000000000002',
    availability_status = 'AVAILABLE',
    min_reward = 1600.00,
    reliability_score = 90,
    average_rating = 4.80,
    completed_orders_count = 27,
    failed_orders_count = 1,
    updated_at = now()
WHERE id = '72000000-0000-0000-0000-000000000001';

UPDATE bounty.hunter_profiles
SET
    callsign = 'IG-Rho Unit',
    bio = 'Droid contractor specialized in surveillance, precise route mapping, target prediction and sealed-zone entry.',
    faction_id = '40000000-0000-0000-0000-000000000001',
    home_planet_id = '60000000-0000-0000-0000-000000000004',
    availability_status = 'BUSY',
    min_reward = 1200.00,
    reliability_score = 93,
    average_rating = 4.90,
    completed_orders_count = 34,
    failed_orders_count = 0,
    updated_at = now()
WHERE id = '72000000-0000-0000-0000-000000000002';

UPDATE bounty.hunter_profiles
SET
    callsign = 'Specter Wrench',
    bio = 'Engineer-pilot taking salvage, convoy escort and emergency repair contracts near shipyard and refinery systems.',
    faction_id = '40000000-0000-0000-0000-000000000005',
    home_planet_id = '60000000-0000-0000-0000-000000000001',
    availability_status = 'AVAILABLE',
    min_reward = 900.00,
    reliability_score = 80,
    average_rating = 4.50,
    completed_orders_count = 16,
    failed_orders_count = 2,
    updated_at = now()
WHERE id = '72000000-0000-0000-0000-000000000003';

INSERT INTO bounty.hunter_skills (hunter_profile_id, skill_id, level)
VALUES
    ('72000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000009', 84),
    ('72000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000011', 76),
    ('72000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000009', 89),
    ('72000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000010', 96),
    ('72000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000008', 68),
    ('72000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000010', 73)
ON CONFLICT (hunter_profile_id, skill_id) DO UPDATE SET
    level = EXCLUDED.level;
