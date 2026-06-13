ALTER TABLE IF EXISTS bounty.chats
    DROP CONSTRAINT IF EXISTS chats_order_id_fkey;

ALTER TABLE IF EXISTS bounty.complaints
    DROP CONSTRAINT IF EXISTS complaints_order_id_fkey;

ALTER TABLE IF EXISTS bounty.notifications
    DROP CONSTRAINT IF EXISTS notifications_related_order_id_fkey;

ALTER TABLE IF EXISTS bounty.reviews
    DROP CONSTRAINT IF EXISTS reviews_order_id_fkey;

ALTER TABLE IF EXISTS bounty.order_applications
    DROP CONSTRAINT IF EXISTS order_applications_order_id_fkey;

ALTER TABLE IF EXISTS bounty.order_offers
    DROP CONSTRAINT IF EXISTS order_offers_order_id_fkey;

ALTER TABLE IF EXISTS bounty.orders
    RENAME TO legacy_orders;

ALTER TABLE IF EXISTS bounty.order_applications
    RENAME TO legacy_order_applications;

ALTER TABLE IF EXISTS bounty.order_offers
    RENAME TO legacy_order_offers;

ALTER TABLE IF EXISTS bounty.outbox_events
    RENAME TO legacy_outbox_events;
