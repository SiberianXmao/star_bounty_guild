INSERT INTO bounty.orders (
    id,
    client_id,
    assigned_hunter_id,
    title,
    description,
    category_id,
    reward_amount,
    reward_currency_code,
    planet_id,
    sector_id,
    risk_level,
    urgency_level,
    status,
    visibility,
    acceptance_mode,
    requirements,
    deadline,
    created_at,
    updated_at,
    published_at,
    completed_at
)
VALUES
    (
        '80000000-0000-0000-0000-000000000001',
        '71000000-0000-0000-0000-000000000001',
        null,
        'Scout route through Shadow Nebula',
        'Collect safe-passage data through an unstable nebula region. This is a training contract without real hazardous action.',
        '20000000-0000-0000-0000-000000000001',
        2500.00,
        'CREDITS',
        '60000000-0000-0000-0000-000000000003',
        '50000000-0000-0000-0000-000000000003',
        'HIGH',
        'HIGH',
        'OPEN',
        'PUBLIC',
        'APPLICATIONS',
        'Tracking, navigation and unstable-sector experience are preferred.',
        now() + interval '7 days',
        now() - interval '2 days',
        now() - interval '2 days',
        now() - interval '2 days',
        null
    ),
    (
        '80000000-0000-0000-0000-000000000002',
        '71000000-0000-0000-0000-000000000002',
        '72000000-0000-0000-0000-000000000003',
        'Escort cargo to Kuat Docks',
        'Escort a batch of engineering modules to the repair docks.',
        '20000000-0000-0000-0000-000000000003',
        1800.00,
        'REPUBLIC_CREDIT',
        '60000000-0000-0000-0000-000000000004',
        '50000000-0000-0000-0000-000000000004',
        'MEDIUM',
        'NORMAL',
        'IN_PROGRESS',
        'PUBLIC',
        'APPLICATIONS',
        'Pilot or engineer with escort experience required.',
        now() + interval '4 days',
        now() - interval '5 days',
        now() - interval '1 day',
        now() - interval '5 days',
        null
    ),
    (
        '80000000-0000-0000-0000-000000000003',
        '71000000-0000-0000-0000-000000000002',
        '72000000-0000-0000-0000-000000000001',
        'Recover a lost navigation module',
        'Archived contract: the module was found and returned to the client.',
        '20000000-0000-0000-0000-000000000004',
        3200.00,
        'CREDITS',
        '60000000-0000-0000-0000-000000000005',
        '50000000-0000-0000-0000-000000000002',
        'MEDIUM',
        'HIGH',
        'COMPLETED',
        'PUBLIC',
        'APPLICATIONS',
        'Artifact-search experience and work on frontier worlds were required.',
        now() - interval '2 days',
        now() - interval '12 days',
        now() - interval '1 day',
        now() - interval '12 days',
        now() - interval '2 days'
    ),
    (
        '80000000-0000-0000-0000-000000000004',
        '71000000-0000-0000-0000-000000000001',
        null,
        'Personal offer: relay diagnostics',
        'Inspect a training relay node and prepare a system-state report.',
        '20000000-0000-0000-0000-000000000005',
        1500.00,
        'IMPERIAL_CREDIT',
        '60000000-0000-0000-0000-000000000006',
        '50000000-0000-0000-0000-000000000003',
        'EXTREME',
        'CRITICAL',
        'OFFERED',
        'PRIVATE',
        'PERSONAL_OFFER',
        'Technical specialist with diagnostic experience required.',
        now() + interval '2 days',
        now() - interval '1 day',
        now() - interval '1 day',
        null,
        null
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO bounty.order_applications (
    id,
    order_id,
    hunter_id,
    message,
    proposed_reward,
    status
)
VALUES
    (
        '81000000-0000-0000-0000-000000000001',
        '80000000-0000-0000-0000-000000000001',
        '72000000-0000-0000-0000-000000000001',
        'Ready to scout the route. I have experience with unstable sectors.',
        2700.00,
        'PENDING'
    ),
    (
        '81000000-0000-0000-0000-000000000002',
        '80000000-0000-0000-0000-000000000001',
        '72000000-0000-0000-0000-000000000002',
        'I can analyze the route and prepare a risk map.',
        2500.00,
        'PENDING'
    ),
    (
        '81000000-0000-0000-0000-000000000003',
        '80000000-0000-0000-0000-000000000002',
        '72000000-0000-0000-0000-000000000003',
        'I will escort the cargo and handle technical support during the trip.',
        1800.00,
        'ACCEPTED'
    )
ON CONFLICT (order_id, hunter_id) DO NOTHING;

INSERT INTO bounty.order_offers (
    id,
    order_id,
    client_id,
    hunter_id,
    message,
    status,
    expires_at
)
VALUES
    (
        '82000000-0000-0000-0000-000000000001',
        '80000000-0000-0000-0000-000000000004',
        '71000000-0000-0000-0000-000000000001',
        '72000000-0000-0000-0000-000000000003',
        'Need a technical specialist. This offer was sent directly.',
        'PENDING',
        now() + interval '2 days'
    )
ON CONFLICT (order_id, hunter_id) DO NOTHING;
