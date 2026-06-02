import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Crosshair, Search } from "lucide-react";
import EmptyState from "../../components/ui/EmptyState.jsx";
import HunterCard from "../../components/hunters/HunterCard.jsx";
import { dictionaryApi, profilesApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import { AVAILABILITY_OPTIONS } from "../../utils/labels.js";
import styles from "./HuntersPage.module.css";

export default function HuntersPage() {
  const [filters, setFilters] = useState({
    q: "",
    availabilityStatus: "",
    skillId: "",
  });

  const huntersQuery = useQuery({
    queryKey: ["hunters"],
    queryFn: profilesApi.hunters,
  });

  const dictionariesQuery = useQuery({
    queryKey: ["dictionaries"],
    queryFn: dictionaryApi.all,
  });

  const hunters = huntersQuery.data ?? [];
  const dictionaries = dictionariesQuery.data ?? {};

  const filteredHunters = useMemo(() => {
    const search = filters.q.trim().toLowerCase();

    return hunters.filter((hunter) => {
      const matchesText =
        !search ||
        hunter.callsign?.toLowerCase().includes(search) ||
        hunter.bio?.toLowerCase().includes(search) ||
        hunter.factionName?.toLowerCase().includes(search);

      const matchesStatus =
        !filters.availabilityStatus || hunter.availabilityStatus === filters.availabilityStatus;

      const matchesSkill =
        !filters.skillId ||
        hunter.skills?.some((skill) => skill.skillId === filters.skillId);

      return matchesText && matchesStatus && matchesSkill;
    });
  }, [filters, hunters]);

  const updateFilter = (event) => {
    const { name, value } = event.target;
    setFilters((current) => ({
      ...current,
      [name]: value,
    }));
  };

  return (
    <div className={styles.page}>
      <section className={styles.header}>
        <div>
          <span className={styles.kicker}>Registry</span>
          <h1>Охотники</h1>
        </div>
        <div className={styles.headerStats}>
          <span>
            <b>{hunters.length}</b>
            в реестре
          </span>
          <span>
            <b>{hunters.filter((hunter) => hunter.availabilityStatus === "AVAILABLE").length}</b>
            доступны
          </span>
        </div>
      </section>

      <section className={styles.tools}>
        <label className="field">
          <span>Поиск</span>
          <div className={styles.searchField}>
            <Search size={17} aria-hidden="true" />
            <input
              className="input"
              name="q"
              value={filters.q}
              onChange={updateFilter}
              placeholder="позывной, фракция"
            />
          </div>
        </label>

        <label className="field">
          <span>Доступность</span>
          <select
            className="select"
            name="availabilityStatus"
            value={filters.availabilityStatus}
            onChange={updateFilter}
          >
            <option value="">Любая</option>
            {AVAILABILITY_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="field">
          <span>Навык</span>
          <select
            className="select"
            name="skillId"
            value={filters.skillId}
            onChange={updateFilter}
          >
            <option value="">Любой</option>
            {dictionaries.skills?.map((skill) => (
              <option key={skill.id} value={skill.id}>
                {skill.name}
              </option>
            ))}
          </select>
        </label>
      </section>

      {huntersQuery.isError ? (
        <div className="notice error">{getApiErrorMessage(huntersQuery.error)}</div>
      ) : null}

      {huntersQuery.isLoading ? (
        <div className={styles.grid}>
          {Array.from({ length: 6 }).map((_, index) => (
            <div className={styles.skeleton} key={index} />
          ))}
        </div>
      ) : filteredHunters.length > 0 ? (
        <div className={styles.grid}>
          {filteredHunters.map((hunter) => (
            <HunterCard hunter={hunter} key={hunter.id} />
          ))}
        </div>
      ) : (
        <EmptyState icon={Crosshair} title="Охотники не найдены" text="Проверь фильтры реестра." />
      )}
    </div>
  );
}
