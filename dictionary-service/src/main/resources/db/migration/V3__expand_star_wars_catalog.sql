INSERT INTO bounty.currencies (code, name, symbol, is_active)
VALUES
    ('CREDITS', 'Galactic Credit Standard', 'CR', true),
    ('REPUBLIC_CREDIT', 'New Republic Credit', 'NRC', true),
    ('IMPERIAL_CREDIT', 'Imperial Credit', 'IC', true),
    ('BESKAR', 'Beskar Ingot', 'BSK', true),
    ('HUTT_WUPIUPI', 'Hutt Wupiupi', 'WUP', true),
    ('CALAMARI_FLAN', 'Mon Calamari Flan', 'FLAN', true),
    ('CREDIT_CHIP', 'Encoded Credit Chip', 'CHIP', true),
    ('AURODIUM_BAR', 'Aurodium Bar', 'AU', true)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    symbol = EXCLUDED.symbol,
    is_active = EXCLUDED.is_active;

INSERT INTO bounty.order_categories (id, name, slug, description, is_active)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'Reconnaissance', 'recon', 'Scouting routes, outposts, battlefields and restricted orbital lanes.', true),
    ('20000000-0000-0000-0000-000000000002', 'Secure Delivery', 'delivery', 'Transport of sealed cargo, encrypted data cores, medical supplies or ship parts.', true),
    ('20000000-0000-0000-0000-000000000003', 'Escort Duty', 'escort', 'Protection for caravans, freighters, diplomats and guild witnesses.', true),
    ('20000000-0000-0000-0000-000000000004', 'Artifact Recovery', 'artifact-search', 'Recovery of relics, navigation modules, holocrons and pre-Imperial technology.', true),
    ('20000000-0000-0000-0000-000000000005', 'Field Engineering', 'technical-support', 'Repairs, diagnostics, slicing support and emergency system restoration.', true),
    ('20000000-0000-0000-0000-000000000006', 'Diplomatic Protection', 'diplomatic-mission', 'Protection and controlled extraction during negotiations or fragile truces.', true),
    ('20000000-0000-0000-0000-000000000007', 'Fugitive Recovery', 'fugitive-recovery', 'Tracking and lawful capture of wanted fugitives, deserters or debt runners.', true),
    ('20000000-0000-0000-0000-000000000008', 'Salvage Claim', 'salvage-claim', 'Inspection and recovery of ships, cargo pods and wreckage in contested space.', true),
    ('20000000-0000-0000-0000-000000000009', 'Droid Retrieval', 'droid-retrieval', 'Capture, extraction or repair of lost droids and autonomous assets.', true),
    ('20000000-0000-0000-0000-000000000010', 'Syndicate Interdiction', 'syndicate-interdiction', 'Disruption of pirate, cartel or black-market operations under guild contract.', true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    description = EXCLUDED.description,
    is_active = EXCLUDED.is_active,
    updated_at = now();

INSERT INTO bounty.skills (id, name, description)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'Piloting', 'Atmospheric and hyperspace flight, evasive maneuvers and docking under fire.'),
    ('30000000-0000-0000-0000-000000000002', 'Survival', 'Operations in deserts, ice fields, toxic ruins and low-supply frontier regions.'),
    ('30000000-0000-0000-0000-000000000003', 'Negotiation', 'Client handling, prisoner exchange, settlement disputes and controlled intimidation.'),
    ('30000000-0000-0000-0000-000000000004', 'Tracking', 'Target pursuit, spoor reading, sensor interpretation and underworld contact work.'),
    ('30000000-0000-0000-0000-000000000005', 'Engineering', 'Ship repair, shield work, reactor diagnostics and field fabrication.'),
    ('30000000-0000-0000-0000-000000000006', 'Slicing', 'Security bypass, data extraction, signal masking and counter-intrusion.'),
    ('30000000-0000-0000-0000-000000000007', 'Astrogation', 'Hyperspace route planning, hazard charts and navicomputer recovery.'),
    ('30000000-0000-0000-0000-000000000008', 'Combat Medicine', 'Trauma care, bacta stabilization and evacuation triage.'),
    ('30000000-0000-0000-0000-000000000009', 'Stealth', 'Silent entry, surveillance discipline and low-profile extraction.'),
    ('30000000-0000-0000-0000-000000000010', 'Droid Handling', 'Droid diagnostics, restraining-bolt work and behavioral reset protocols.'),
    ('30000000-0000-0000-0000-000000000011', 'Heavy Weapons', 'Crew-served weapons, breaching tools and armored target suppression.'),
    ('30000000-0000-0000-0000-000000000012', 'Xenobiology', 'Creature identification, venom response and non-human physiology.')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description;

INSERT INTO bounty.factions (
    id,
    name,
    description,
    type,
    influence_level,
    relation_to_guild
)
VALUES
    ('40000000-0000-0000-0000-000000000001', 'Bounty Hunters Guild', 'Neutral guild network that brokers contracts, arbitrates disputes and tracks hunter standing.', 'GUILD', 88, 'FRIENDLY'),
    ('40000000-0000-0000-0000-000000000002', 'New Republic', 'Galactic government authority controlling key Core and Mid Rim routes.', 'GOVERNMENT', 80, 'ALLIED'),
    ('40000000-0000-0000-0000-000000000003', 'Imperial Remnant', 'Fragmented Imperial cells, warlords and hidden logistics groups.', 'GOVERNMENT', 58, 'HOSTILE'),
    ('40000000-0000-0000-0000-000000000004', 'Hutt Cartel', 'Cartel families controlling spice, debt, gambling and private enforcement in Hutt Space.', 'PIRATES', 78, 'HOSTILE'),
    ('40000000-0000-0000-0000-000000000005', 'Corellian Engineering Guild', 'Shipwrights, dockmasters and freight engineers tied to Corellian yards.', 'CORPORATION', 64, 'NEUTRAL'),
    ('40000000-0000-0000-0000-000000000006', 'Frontier Worlds Union', 'Loose frontier coalition that pays for protection where patrols rarely arrive.', 'REBELS', 52, 'FRIENDLY'),
    ('40000000-0000-0000-0000-000000000007', 'Black Sun', 'Criminal syndicate specializing in smuggling, assassination and political leverage.', 'PIRATES', 70, 'HOSTILE'),
    ('40000000-0000-0000-0000-000000000008', 'Pyke Syndicate', 'Spice-running organization with fortified routes and disciplined enforcers.', 'PIRATES', 66, 'HOSTILE'),
    ('40000000-0000-0000-0000-000000000009', 'Mandalorian Covert', 'Scattered Mandalorian cells with strict codes, armor traditions and private contracts.', 'NEUTRAL', 45, 'NEUTRAL'),
    ('40000000-0000-0000-0000-000000000010', 'Mining Guild', 'Industrial resource consortium with legal claims and hired security forces.', 'CORPORATION', 59, 'NEUTRAL')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    type = EXCLUDED.type,
    influence_level = EXCLUDED.influence_level,
    relation_to_guild = EXCLUDED.relation_to_guild,
    updated_at = now();

INSERT INTO bounty.sectors (
    id,
    controlling_faction_id,
    name,
    description,
    stability_level,
    danger_level
)
VALUES
    ('50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000005', 'Corellian Trade Spine', 'High-traffic freight lane with shipyards, guild docks and corporate patrols.', 76, 32),
    ('50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Outer Rim Frontier', 'Sparse settlements, long response times and frequent private protection contracts.', 40, 72),
    ('50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000007', 'Shadow Nebula', 'Sensor-poor nebula routes used by raiders, fugitives and silent cargo haulers.', 22, 90),
    ('50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000005', 'Kuat Industrial Ring', 'Orbital docks, ship component markets and heavily monitored repair corridors.', 68, 42),
    ('50000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000004', 'Hutt Space', 'Cartel-controlled trade volume, palace courts and dangerous debt enforcement.', 34, 82),
    ('50000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000009', 'Mandalore Sector', 'Scattered clan holdings, old battlefields and restricted beskar claims.', 44, 76),
    ('50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000002', 'Core Worlds', 'Administrative centers, strict law enforcement and high-value political traffic.', 86, 24),
    ('50000000-0000-0000-0000-000000000008', '40000000-0000-0000-0000-000000000010', 'Corporate Sector', 'Privately policed mining systems, refinery chains and contract-heavy stations.', 62, 58),
    ('50000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000008', 'Spice Run', 'Hidden lanes, spice convoys and Pyke-controlled refueling points.', 28, 87),
    ('50000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000003', 'Anoat Expanse', 'Cold lanes, abandoned depots and Imperial salvage activity.', 36, 80)
ON CONFLICT (id) DO UPDATE SET
    controlling_faction_id = EXCLUDED.controlling_faction_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    stability_level = EXCLUDED.stability_level,
    danger_level = EXCLUDED.danger_level,
    updated_at = now();

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
    ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000005', 'Corellia', 'Shipbuilding world, pilot culture center and major guild logistics stop.', 34, 91, 'temperate industrial', 3000000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000004', 'Tatooine', 'Twin-sun desert world of moisture farms, cantinas, syndicate brokers and hidden jobs.', 82, 36, 'desert', 200000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000004', 'Nar Shaddaa', 'Smuggler moon with endless vertical cities, debt records and cartel safehouses.', 92, 84, 'urban moon', 85000000, 'RESTRICTED'),
    ('60000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000005', 'Kuat', 'Orbital shipyards, corporate escorts and sensitive prototype cargo.', 44, 96, 'orbital industrial', 120000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Lothal', 'Frontier world with plains, old Imperial facilities and recovering trade links.', 54, 60, 'grassland', 6000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000003', 'Bespin', 'Gas giant with tibanna facilities, floating platforms and sabotage risk.', 58, 78, 'gas giant platforms', 6000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000002', 'Coruscant', 'Galactic capital world with dense security, politics and deep underworld levels.', 38, 100, 'ecumenopolis', 1000000000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000008', '50000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000009', 'Mandalore', 'Scarred homeworld with clan claims, old siege zones and restricted armor routes.', 86, 48, 'arid wasteland', 4000000, 'RESTRICTED'),
    ('60000000-0000-0000-0000-000000000009', '50000000-0000-0000-0000-000000000008', '40000000-0000-0000-0000-000000000010', 'Ord Mantell', 'Scrap markets, mercenary docks and shifting guild offices.', 74, 63, 'temperate scrapyards', 4000000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000010', '50000000-0000-0000-0000-000000000009', '40000000-0000-0000-0000-000000000008', 'Kessel', 'Mining world and spice route anchor with dangerous labor zones.', 90, 57, 'mining tunnels', 1000000, 'RESTRICTED'),
    ('60000000-0000-0000-0000-000000000011', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Nevarro', 'Lava-scarred frontier trade hub with guild history and rapid reconstruction.', 62, 52, 'volcanic plains', 900000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000012', '50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000007', 'Dathomir', 'Isolated world of red mists, predators and abandoned strongholds.', 95, 22, 'red jungle', 5200, 'RESTRICTED'),
    ('60000000-0000-0000-0000-000000000013', '50000000-0000-0000-0000-000000000010', '40000000-0000-0000-0000-000000000003', 'Hoth', 'Frozen remote world with abandoned bases and harsh survival contracts.', 79, 12, 'ice', 0, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000014', '50000000-0000-0000-0000-000000000008', '40000000-0000-0000-0000-000000000010', 'Mustafar', 'Volcanic refinery world, fortress ruins and extreme environmental hazards.', 98, 44, 'volcanic', 20000, 'RESTRICTED'),
    ('60000000-0000-0000-0000-000000000015', '50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000002', 'Mon Cala', 'Ocean world of shipyards, diplomacy and underwater urban centers.', 36, 82, 'oceanic', 27000000000, 'ACTIVE'),
    ('60000000-0000-0000-0000-000000000016', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', 'Ryloth', 'Wind-swept frontier world with clan politics, caves and smuggling pressure.', 66, 46, 'arid mesas', 1500000000, 'ACTIVE')
ON CONFLICT (id) DO UPDATE SET
    sector_id = EXCLUDED.sector_id,
    controlling_faction_id = EXCLUDED.controlling_faction_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    danger_level = EXCLUDED.danger_level,
    development_level = EXCLUDED.development_level,
    climate = EXCLUDED.climate,
    population = EXCLUDED.population,
    status = EXCLUDED.status,
    updated_at = now();
