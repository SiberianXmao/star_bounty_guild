INSERT INTO bounty.currencies (code, name, symbol, is_active)
VALUES
    ('CREDITS', 'Galactic Credits', 'CR', true),
    ('REPUBLIC_CREDIT', 'Republic Credit', 'RC', true),
    ('IMPERIAL_CREDIT', 'Imperial Credit', 'IC', true),
    ('BESKAR', 'Beskar Ingots', 'BSK', true)
ON CONFLICT (code) DO NOTHING;

INSERT INTO bounty.order_categories (id, name, slug, description, is_active)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'Recon', 'recon', 'Information gathering, surveillance and territory scouting', true),
    ('20000000-0000-0000-0000-000000000002', 'Delivery', 'delivery', 'Transport of cargo, data or specialized equipment', true),
    ('20000000-0000-0000-0000-000000000003', 'Escort', 'escort', 'Escort of caravans, ships or important persons', true),
    ('20000000-0000-0000-0000-000000000004', 'Artifact Search', 'artifact-search', 'Search for rare items, relics and lost technologies', true),
    ('20000000-0000-0000-0000-000000000005', 'Technical Support', 'technical-support', 'Repair, diagnostics and field engineering', true),
    ('20000000-0000-0000-0000-000000000006', 'Diplomatic Mission', 'diplomatic-mission', 'Negotiations, delegation escort and conflict resolution', true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO bounty.skills (id, name, description)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'Piloting', 'Ship control, maneuvers and navigation'),
    ('30000000-0000-0000-0000-000000000002', 'Survival', 'Work in hostile environments and dangerous planets'),
    ('30000000-0000-0000-0000-000000000003', 'Negotiation', 'Diplomacy, persuasion and work with clients'),
    ('30000000-0000-0000-0000-000000000004', 'Tracking', 'Surveillance, stealth movement and target search'),
    ('30000000-0000-0000-0000-000000000005', 'Engineering', 'Repair, diagnostics, droid and equipment work'),
    ('30000000-0000-0000-0000-000000000006', 'Cybersecurity', 'Systems intrusion, data protection and signal analysis'),
    ('30000000-0000-0000-0000-000000000007', 'Cartography', 'Navigation, route planning and sector mapping'),
    ('30000000-0000-0000-0000-000000000008', 'Medicine', 'First aid and medical support during dangerous missions')
ON CONFLICT (name) DO NOTHING;

INSERT INTO bounty.factions (
    id,
    name,
    description,
    type,
    influence_level,
    relation_to_guild
)
VALUES
    ('40000000-0000-0000-0000-000000000001', 'Free Hunters Guild', 'Neutral guild of independent contract executors', 'GUILD', 85, 'FRIENDLY'),
    ('40000000-0000-0000-0000-000000000002', 'New Republic', 'Government force controlling central trade routes', 'GOVERNMENT', 75, 'ALLIED'),
    ('40000000-0000-0000-0000-000000000003', 'Imperial Remnant', 'Scattered military cells of the former Imperial fleet', 'GOVERNMENT', 55, 'HOSTILE'),
    ('40000000-0000-0000-0000-000000000004', 'Black Sun Syndicate', 'Criminal trade network operating on the outer edges', 'PIRATES', 65, 'HOSTILE'),
    ('40000000-0000-0000-0000-000000000005', 'Corellian Transport League', 'Corporate alliance of carriers and engineers', 'CORPORATION', 60, 'NEUTRAL'),
    ('40000000-0000-0000-0000-000000000006', 'Frontier Worlds Union', 'Loose alliance of outer rim colonies', 'REBELS', 50, 'FRIENDLY')
ON CONFLICT (name) DO NOTHING;

INSERT INTO bounty.sectors (
    id,
    controlling_faction_id,
    name,
    description,
    stability_level,
    danger_level
)
VALUES
    ('50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000002', 'Corellian Trade Spine', 'Major trade sector with active carrier routes', 72, 35),
    ('50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Outer Rim Frontier', 'Frontier sector with sparse patrols and independent settlements', 38, 70),
    ('50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000004', 'Shadow Nebula', 'Dangerous sector with pirate bases and unstable routes', 20, 88),
    ('50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000005', 'Industrial Belt', 'Industrial shipyard, mine and repair dock region', 61, 45)
ON CONFLICT (name) DO NOTHING;

INSERT INTO bounty.planets (
    id,
    sector_id,
    controlling_faction_id,
    name,
    description,
    danger_level,
    development_level,
    climate,
    population,
    status
)
VALUES
    ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000005', 'Corellia', 'Industrial world with strong engineering schools and ship infrastructure', 35, 90, 'temperate', 3000000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Tatooine Outpost', 'Desert outpost on the edge of trade routes', 78, 35, 'desert', 200000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000004', 'Nar Shaddaa Station', 'Dense orbital city with contracts, debt and dubious deals', 90, 80, 'urban orbital', 85000000, 'RESTRICTED'),
    ('60000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000005', 'Kuat Docks', 'Orbital docks and repair capacities for mid-class ships', 45, 95, 'orbital industrial', 120000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Lothal Frontier', 'Agrarian-industrial frontier world with growing contract activity', 55, 58, 'grassland', 6000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', 'Dromund Relay', 'Restricted relay node with high control and limited access', 82, 70, 'storm', 1000000, 'RESTRICTED')
ON CONFLICT (name) DO NOTHING;
