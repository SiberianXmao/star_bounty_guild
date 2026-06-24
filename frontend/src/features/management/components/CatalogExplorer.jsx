import { useMemo, useState } from "react";
import { Database, Search } from "lucide-react";
import StatusBadge from "../../../components/ui/StatusBadge.jsx";
import { getApiErrorMessage } from "../../../services/apiClient.js";
import { dictionaryLabel } from "../labels.js";
import { buildCatalogModel } from "../utils.js";
import PanelTitle from "./PanelTitle.jsx";
import styles from "../ManagementConsole.module.css";
import PlanetImageUploader from "../../planet-image/PlanetImageUploader.jsx";

export default function CatalogExplorer({ dictionaries, error, isError, isLoading }) {
  const [search, setSearch] = useState("");
  const catalog = useMemo(() => buildCatalogModel(dictionaries), [dictionaries]);
  const normalizedSearch = search.trim().toLowerCase();

  const sectors = useMemo(
    () =>
      catalog.sectors.filter((sector) =>
        hasMatch(normalizedSearch, [
          sector.name,
          sector.description,
          sector.controllingFaction?.name,
          ...sector.planets.map((planet) => planet.name),
        ]),
      ),
    [catalog.sectors, normalizedSearch],
  );

  const factions = useMemo(
    () =>
      catalog.factions.filter((faction) =>
        hasMatch(normalizedSearch, [
          faction.name,
          faction.description,
          faction.type,
          faction.relationToGuild,
          ...faction.controlledSectors.map((sector) => sector.name),
          ...faction.controlledPlanets.map((planet) => planet.name),
        ]),
      ),
    [catalog.factions, normalizedSearch],
  );

  return (
    <section className={styles.catalogPanel}>
      <PanelTitle icon={Database} title="Карта справочников" />

      <div className={styles.catalogToolbar}>
        <label className="field">
          <span>Поиск</span>
          <div className={styles.searchField}>
            <Search size={17} aria-hidden="true" />
            <input
              className="input"
              placeholder="сектор, планета, фракция"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
          </div>
        </label>
        <div className={styles.miniStat}>
          <b>{catalog.planets.length}</b>
          планет в реестре
        </div>
      </div>

      {isError ? <div className="notice error">{getApiErrorMessage(error)}</div> : null}
      {isLoading ? <div className={styles.loading}>Загрузка справочников...</div> : null}

      {!isLoading ? (
        <>
          <div className={styles.catalogSummary}>
            <SummaryStat label="Фракции" value={catalog.factions.length} />
            <SummaryStat label="Секторы" value={catalog.sectors.length} />
            <SummaryStat label="Планеты" value={catalog.planets.length} />
            <SummaryStat
              label="Навыки"
              value={(dictionaries.skills ?? []).length}
            />
          </div>

          <div className={styles.catalogColumns}>
            <div className={styles.catalogColumn}>
              <h3>Секторы и планеты</h3>
              {sectors.length ? (
                sectors.map((sector) => <SectorCard key={sector.id} sector={sector} />)
              ) : (
                <p className={styles.emptyLine}>Секторы не найдены</p>
              )}
            </div>

            <div className={styles.catalogColumn}>
              <h3>Фракционный контроль</h3>
              {factions.length ? (
                factions.map((faction) => <FactionCard faction={faction} key={faction.id} />)
              ) : (
                <p className={styles.emptyLine}>Фракции не найдены</p>
              )}
            </div>
          </div>

          <ReferenceShelf dictionaries={dictionaries} />
        </>
      ) : null}
    </section>
  );
}

function SummaryStat({ label, value }) {
  return (
    <span className={styles.miniStat}>
      <b>{value}</b>
      {label}
    </span>
  );
}

function SectorCard({ sector }) {
  return (
    <article className={styles.sectorCard}>
      <div className={styles.entityHead}>
        <div>
          <strong>{sector.name}</strong>
          <span>{sector.controllingFaction?.name ?? "Нейтральный контроль"}</span>
        </div>
        <StatusBadge tone={sector.dangerLevel > 70 ? "danger" : "neutral"}>
          Риск {sector.dangerLevel}
        </StatusBadge>
      </div>
      <div className={styles.entityMeta}>
        <span>Стабильность {sector.stabilityLevel}</span>
        <span>{sector.planets.length} планет</span>
      </div>
      <PlanetImageUploader entity={sector} type="sectors" />
      {sector.planets.length ? (
        <ul className={styles.planetList}>
          {sector.planets.map((planet) => (
            <li key={planet.id}>
              <strong>{planet.name}</strong>
              <small>
                {dictionaryLabel(planet.status)} · риск {planet.dangerLevel} · развитие{" "}
                {planet.developmentLevel}
              </small>
              <PlanetImageUploader entity={planet} type="planets" />
            </li>
          ))}
        </ul>
      ) : (
        <p className={styles.emptyLine}>Планет нет</p>
      )}
    </article>
  );
}

function FactionCard({ faction }) {
  return (
    <article className={styles.factionCard}>
      <div className={styles.entityHead}>
        <div>
          <strong>{faction.name}</strong>
          <span>{dictionaryLabel(faction.type)} · {dictionaryLabel(faction.relationToGuild)}</span>
        </div>
        <StatusBadge tone={faction.relationToGuild === "HOSTILE" ? "danger" : "neutral"}>
          {faction.influenceLevel}
        </StatusBadge>
      </div>
      <div className={styles.entityMeta}>
        <span>{faction.controlledSectors.length} секторов</span>
        <span>{faction.controlledPlanets.length} планет</span>
      </div>
      <PlanetImageUploader entity={faction} type="factions" />
      <AssetList items={faction.controlledSectors} label="Секторы" />
      <AssetList items={faction.controlledPlanets} label="Планеты" />
    </article>
  );
}

function AssetList({ items, label }) {
  if (!items.length) {
    return <p className={styles.emptyLine}>{label}: нет</p>;
  }

  return (
    <ul className={styles.assetList} aria-label={label}>
      {items.map((item) => (
        <li key={item.id}>
          {item.name}
          {"status" in item ? <small>{dictionaryLabel(item.status)}</small> : null}
        </li>
      ))}
    </ul>
  );
}

function ReferenceShelf({ dictionaries }) {
  return (
    <div className={styles.referenceShelf}>
      <ReferenceGroup items={dictionaries.categories ?? []} title="Категории" valueKey="name" detailKey="slug" />
      <ReferenceGroup items={dictionaries.currencies ?? []} title="Валюты" valueKey="code" detailKey="name" />
      <ReferenceGroup items={dictionaries.skills ?? []} title="Навыки" valueKey="name" />
    </div>
  );
}

function ReferenceGroup({ detailKey, items, title, valueKey }) {
  return (
    <section className={styles.referenceGroup}>
      <h3>{title}</h3>
      {items.length ? (
        <ul className={styles.referenceList}>
          {items.map((item) => (
            <li key={item.id ?? item.code}>
              {item[valueKey]}
              {detailKey ? <small>{item[detailKey]}</small> : null}
            </li>
          ))}
        </ul>
      ) : (
        <p className={styles.emptyLine}>Пусто</p>
      )}
    </section>
  );
}

function hasMatch(search, values) {
  if (!search) {
    return true;
  }

  return values.some((value) => String(value ?? "").toLowerCase().includes(search));
}
