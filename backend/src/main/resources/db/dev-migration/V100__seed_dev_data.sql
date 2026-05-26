-- V100__seed_dev_data.sql
-- Dev-данные только для локальной разработки.
-- Запускать через профиль dev.

-- Пароль для всех demo-пользователей: password
-- BCrypt hash для тестового пароля.
-- При необходимости заменить на хеш, созданный твоим PasswordEncoder.
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- =========================
-- Users
-- =========================

INSERT INTO bounty.users (
    id,
    email,
    password_hash,
    username,
    display_name,
    avatar_url,
    status,
    email_verified,
    created_at,
    updated_at,
    last_login_at
)
VALUES
    (
        '70000000-0000-0000-0000-000000000001',
        'admin@bounty.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'admin',
        'Guild Administrator',
        null,
        'ACTIVE',
        true,
        now() - interval '30 days',
        now() - interval '1 day',
        now() - interval '1 hour'
    ),
    (
        '70000000-0000-0000-0000-000000000002',
        'client.hutt@bounty.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'client_hutt',
        'Outer Rim Patron',
        null,
        'ACTIVE',
        true,
        now() - interval '25 days',
        now() - interval '2 days',
        now() - interval '3 hours'
    ),
    (
        '70000000-0000-0000-0000-000000000003',
        'client.rebel@bounty.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'client_rebel_cell',
        'Frontier Logistics Cell',
        null,
        'ACTIVE',
        true,
        now() - interval '20 days',
        now() - interval '1 day',
        now() - interval '5 hours'
    ),
    (
        '70000000-0000-0000-0000-000000000004',
        'hunter.raven@bounty.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'hunter_raven',
        'Beskar Raven',
        null,
        'ACTIVE',
        true,
        now() - interval '18 days',
        now() - interval '2 hours',
        now() - interval '30 minutes'
    ),
    (
        '70000000-0000-0000-0000-000000000005',
        'hunter.rho@bounty.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'hunter_rho',
        'IG-Rho Unit',
        null,
        'ACTIVE',
        true,
        now() - interval '15 days',
        now() - interval '4 hours',
        now() - interval '2 hours'
    ),
    (
        '70000000-0000-0000-0000-000000000006',
        'hunter.specter@bounty.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'hunter_specter',
        'Specter Wrench',
        null,
        'ACTIVE',
        true,
        now() - interval '10 days',
        now() - interval '1 day',
        now() - interval '6 hours'
    )
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- User roles
-- =========================

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000001', id
FROM bounty.roles
WHERE name = 'ADMIN'
    ON CONFLICT DO NOTHING;

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000001', id
FROM bounty.roles
WHERE name = 'MODERATOR'
    ON CONFLICT DO NOTHING;

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000002', id
FROM bounty.roles
WHERE name = 'CLIENT'
    ON CONFLICT DO NOTHING;

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000003', id
FROM bounty.roles
WHERE name = 'CLIENT'
    ON CONFLICT DO NOTHING;

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000004', id
FROM bounty.roles
WHERE name = 'HUNTER'
    ON CONFLICT DO NOTHING;

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000005', id
FROM bounty.roles
WHERE name = 'HUNTER'
    ON CONFLICT DO NOTHING;

INSERT INTO bounty.user_roles (user_id, role_id)
SELECT '70000000-0000-0000-0000-000000000006', id
FROM bounty.roles
WHERE name = 'HUNTER'
    ON CONFLICT DO NOTHING;

-- =========================
-- Client profiles
-- =========================

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
        'Частный заказчик с большим количеством контрактов на внешних рубежах.',
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
        'Логистическая ячейка пограничных миров. Часто заказывает доставку и сопровождение.',
        '40000000-0000-0000-0000-000000000006',
        '60000000-0000-0000-0000-000000000005',
        81,
        4.70,
        14,
        0
    )
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- Hunter profiles
-- =========================

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
        'Независимый охотник, предпочитает сопровождение, разведку и сложные контракты на внешнем кольце.',
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
        'Дроид-исполнитель для разведки, технического анализа и точной навигации.',
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
        'Инженер-пилот, берёт контракты на ремонт, сопровождение и работу с корабельными системами.',
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

-- =========================
-- Hunter skills
-- =========================

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

-- =========================
-- Orders
-- =========================

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
        'Разведать маршрут через Shadow Nebula',
        'Нужно собрать данные о безопасном проходе через нестабильную область туманности. Контракт учебный, без реальных опасных действий.',
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
        'Желателен опыт слежки, навигации и работы в нестабильных секторах.',
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
        'Сопроводить груз до Kuat Docks',
        'Требуется сопровождение партии инженерных модулей до ремонтных доков.',
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
        'Нужен пилот или инженер с опытом сопровождения.',
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
        'Найти утерянный навигационный модуль',
        'Архивный контракт: модуль найден и возвращён заказчику.',
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
        'Требовался опыт поиска артефактов и работы на пограничных мирах.',
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
        'Персональное предложение: диагностика ретранслятора',
        'Нужно проверить учебный ретрансляционный узел и подготовить отчёт о состоянии систем.',
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
        'Нужен технический специалист с опытом диагностики систем связи.',
        now() + interval '2 days',
        now() - interval '1 day',
        now() - interval '1 day',
        null,
        null
    )
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- Order applications
-- =========================

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
        'Готов взять разведку. Есть опыт работы в нестабильных секторах.',
        2700.00,
        'PENDING'
    ),
    (
        '81000000-0000-0000-0000-000000000002',
        '80000000-0000-0000-0000-000000000001',
        '72000000-0000-0000-0000-000000000002',
        'Могу провести анализ маршрута и подготовить карту рисков.',
        2500.00,
        'PENDING'
    ),
    (
        '81000000-0000-0000-0000-000000000003',
        '80000000-0000-0000-0000-000000000002',
        '72000000-0000-0000-0000-000000000003',
        'Возьму сопровождение груза. Есть опыт технической поддержки в пути.',
        1800.00,
        'ACCEPTED'
    )
    ON CONFLICT (order_id, hunter_id) DO NOTHING;

-- =========================
-- Order offers
-- =========================

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
        'Нужен специалист по технике. Предложение направлено напрямую.',
        'PENDING',
        now() + interval '2 days'
    )
    ON CONFLICT (order_id, hunter_id) DO NOTHING;

-- =========================
-- Chats
-- =========================

INSERT INTO bounty.chats (
    id,
    type,
    order_id,
    direct_pair_key,
    status
)
VALUES
    (
        '83000000-0000-0000-0000-000000000001',
        'ORDER_CHAT',
        '80000000-0000-0000-0000-000000000002',
        null,
        'ACTIVE'
    ),
    (
        '83000000-0000-0000-0000-000000000002',
        'DIRECT_CHAT',
        null,
        '70000000-0000-0000-0000-000000000003:70000000-0000-0000-0000-000000000004',
        'ACTIVE'
    )
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- Chat participants
-- =========================

INSERT INTO bounty.chat_participants (
    chat_id,
    user_id,
    role_in_chat,
    joined_at,
    last_read_at
)
VALUES
    (
        '83000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000003',
        'CLIENT',
        now() - interval '4 days',
        now() - interval '1 hour'
    ),
    (
        '83000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000006',
        'HUNTER',
        now() - interval '4 days',
        now() - interval '30 minutes'
    ),
    (
        '83000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000003',
        'CLIENT',
        now() - interval '3 days',
        now() - interval '2 hours'
    ),
    (
        '83000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000004',
        'HUNTER',
        now() - interval '3 days',
        now() - interval '1 hour'
    )
    ON CONFLICT DO NOTHING;

-- =========================
-- Chat messages
-- =========================

INSERT INTO bounty.chat_messages (
    id,
    chat_id,
    sender_id,
    content,
    message_type,
    created_at
)
VALUES
    (
        '84000000-0000-0000-0000-000000000001',
        '83000000-0000-0000-0000-000000000001',
        null,
        'Системное сообщение: исполнитель выбран, заказ перешёл в работу.',
        'SYSTEM',
        now() - interval '4 days'
    ),
    (
        '84000000-0000-0000-0000-000000000002',
        '83000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000003',
        'Подтверждаю маршрут до Kuat Docks. Нужна проверка контейнеров перед выходом.',
        'USER',
        now() - interval '3 days 23 hours'
    ),
    (
        '84000000-0000-0000-0000-000000000003',
        '83000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000006',
        'Принято. Проведу диагностику креплений и систем охлаждения.',
        'USER',
        now() - interval '3 days 22 hours'
    ),
    (
        '84000000-0000-0000-0000-000000000004',
        '83000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000003',
        'Есть новый маршрут на внешнем кольце. Интересует разведка?',
        'USER',
        now() - interval '2 days'
    ),
    (
        '84000000-0000-0000-0000-000000000005',
        '83000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000004',
        'Да, отправьте детали. Оценю риски и награду.',
        'USER',
        now() - interval '1 day 23 hours'
    )
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- Reviews
-- =========================

INSERT INTO bounty.reviews (
    id,
    order_id,
    author_id,
    target_user_id,
    rating,
    text
)
VALUES
    (
        '85000000-0000-0000-0000-000000000001',
        '80000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000004',
        5,
        'Контракт выполнен аккуратно и в срок. Хорошая связь и отчётность.'
    ),
    (
        '85000000-0000-0000-0000-000000000002',
        '80000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000004',
        '70000000-0000-0000-0000-000000000003',
        5,
        'Заказчик дал понятные условия и быстро подтвердил завершение.'
    )
    ON CONFLICT (order_id, author_id, target_user_id) DO NOTHING;

-- =========================
-- Favorites
-- =========================

INSERT INTO bounty.favorites (
    id,
    user_id,
    target_user_id,
    type
)
VALUES
    (
        '86000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000004',
        'HUNTER'
    ),
    (
        '86000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000004',
        '70000000-0000-0000-0000-000000000003',
        'CLIENT'
    )
    ON CONFLICT (user_id, target_user_id, type) DO NOTHING;

-- =========================
-- Notifications
-- =========================

INSERT INTO bounty.notifications (
    id,
    user_id,
    type,
    title,
    message,
    is_read,
    related_order_id,
    related_chat_id,
    created_at
)
VALUES
    (
        '87000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000002',
        'NEW_APPLICATION',
        'Новый отклик',
        'Beskar Raven откликнулся на разведку маршрута через Shadow Nebula.',
        false,
        '80000000-0000-0000-0000-000000000001',
        null,
        now() - interval '1 day'
    ),
    (
        '87000000-0000-0000-0000-000000000002',
        '70000000-0000-0000-0000-000000000006',
        'APPLICATION_ACCEPTED',
        'Отклик принят',
        'Ваш отклик на сопровождение груза до Kuat Docks принят.',
        true,
        '80000000-0000-0000-0000-000000000002',
        '83000000-0000-0000-0000-000000000001',
        now() - interval '4 days'
    ),
    (
        '87000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000006',
        'NEW_OFFER',
        'Новое персональное предложение',
        'Вам отправлено персональное предложение на диагностику ретранслятора.',
        false,
        '80000000-0000-0000-0000-000000000004',
        null,
        now() - interval '1 day'
    )
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- Complaints
-- =========================

INSERT INTO bounty.complaints (
    id,
    author_id,
    target_user_id,
    order_id,
    reason,
    description,
    status,
    created_at,
    resolved_at
)
VALUES
    (
        '88000000-0000-0000-0000-000000000001',
        '70000000-0000-0000-0000-000000000003',
        '70000000-0000-0000-0000-000000000002',
        null,
        'Подозрительная активность',
        'Тестовая жалоба для проверки админ-панели и статусов модерации.',
        'OPEN',
        now() - interval '12 hours',
        null
    )
    ON CONFLICT (id) DO NOTHING;