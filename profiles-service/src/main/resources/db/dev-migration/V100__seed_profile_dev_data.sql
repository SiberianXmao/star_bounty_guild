INSERT INTO bounty.client_profiles (
    id,
    user_id,
    name,
    description,
    faction_id,
    planet_id,
    reliability_score,
    average_rating,
    completed_orders_count,
    cancelled_orders_count
)
VALUES
    (
        '71000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000002',
        'Outer Rim Patron',
        'Private patron with frequent contracts around the outer rim.',
        '40000000-0000-0000-0000-000000000004',
        '60000000-0000-0000-0000-000000000003',
        62,
        4.20,
        8,
        1
    ),
    (
        '71000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000003',
        'Frontier Logistics Cell',
        'Logistics cell for frontier worlds, often ordering delivery and escort work.',
        '40000000-0000-0000-0000-000000000006',
        '60000000-0000-0000-0000-000000000005',
        81,
        4.70,
        14,
        0
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO bounty.hunter_profiles (
    id,
    user_id,
    callsign,
    bio,
    faction_id,
    home_planet_id,
    availability_status,
    min_reward,
    reliability_score,
    average_rating,
    completed_orders_count,
    failed_orders_count
)
VALUES
    (
        '72000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000004',
        'Beskar Raven',
        'Independent hunter focused on escort, recon and difficult outer rim contracts.',
        '40000000-0000-0000-0000-000000000001',
        '60000000-0000-0000-0000-000000000002',
        'AVAILABLE',
        1200.00,
        88,
        4.80,
        19,
        1
    ),
    (
        '72000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000005',
        'IG-Rho Unit',
        'Droid operator for recon, technical analysis and precise navigation.',
        '40000000-0000-0000-0000-000000000001',
        '60000000-0000-0000-0000-000000000004',
        'BUSY',
        900.00,
        91,
        4.90,
        24,
        0
    ),
    (
        '72000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000006',
        'Specter Wrench',
        'Engineer-pilot taking repair, escort and ship-system contracts.',
        '40000000-0000-0000-0000-000000000005',
        '60000000-0000-0000-0000-000000000001',
        'AVAILABLE',
        700.00,
        76,
        4.40,
        11,
        2
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO bounty.hunter_skills (hunter_profile_id, skill_id, level)
VALUES
    ('72000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 82),
    ('72000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000002', 90),
    ('72000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000004', 78),
    ('72000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000004', 86),
    ('72000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000005', 92),
    ('72000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000006', 88),
    ('72000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000001', 74),
    ('72000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000005', 95),
    ('72000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000007', 71)
ON CONFLICT DO NOTHING;
