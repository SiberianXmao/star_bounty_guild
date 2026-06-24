import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useSearchParams } from "react-router-dom";
import {
  BadgeDollarSign,
  BookOpen,
  BriefcaseBusiness,
  Building2,
  Crosshair,
  Globe2,
  Map as MapIcon,
  Search,
} from "lucide-react";
import DirectoryCard from "../../components/directory/DirectoryCard.jsx";
import EmptyState from "../../components/ui/EmptyState.jsx";
import { dictionaryApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import styles from "./DirectoryPage.module.css";

const sections = [
  { id: "planets", label: "Планеты", icon: Globe2 },
  { id: "sectors", label: "Секторы", icon: MapIcon },
  { id: "factions", label: "Фракции", icon: Building2 },
  { id: "categories", label: "Типы контрактов", icon: BriefcaseBusiness },
  { id: "skills", label: "Навыки", icon: Crosshair },
  { id: "currencies", label: "Валюты", icon: BadgeDollarSign },
];

export default function DirectoryPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialSection = sections.some((section) => section.id === searchParams.get("section"))
    ? searchParams.get("section")
    : "planets";
  const [activeSection, setActiveSection] = useState(initialSection);
  const [search, setSearch] = useState("");

  const dictionaryQuery = useQuery({
    queryKey: ["dictionaries"],
    queryFn: dictionaryApi.all,
  });

  const dictionaries = dictionaryQuery.data ?? {};
  const activeDefinition = sections.find((section) => section.id === activeSection) ?? sections[0];
  const ActiveSectionIcon = activeDefinition.icon;

  const relations = useMemo(
    () => ({
      factions: new Map((dictionaries.factions ?? []).map((item) => [item.id, item])),
      sectors: new Map((dictionaries.sectors ?? []).map((item) => [item.id, item])),
    }),
    [dictionaries.factions, dictionaries.sectors],
  );

  const visibleItems = useMemo(() => {
    const query = search.trim().toLocaleLowerCase("ru-RU");
    const items = dictionaries[activeSection] ?? [];

    if (!query) {
      return items;
    }

    return items.filter((item) => {
      const linkedSector = item.sectorId ? relations.sectors.get(item.sectorId)?.name : "";
      const linkedFaction = item.controllingFactionId
        ? relations.factions.get(item.controllingFactionId)?.name
        : "";
      const values = [...Object.values(item), linkedSector, linkedFaction];

      return values
        .filter((value) => typeof value === "string")
        .some((value) => value.toLocaleLowerCase("ru-RU").includes(query));
    });
  }, [activeSection, dictionaries, relations, search]);

  return (
    <div className={styles.page}>
      <header className={styles.pageHeader}>
        <div>
          <span className={styles.eyebrow}>
            <BookOpen size={16} aria-hidden="true" />
            Архив гильдии
          </span>
          <h1>Справочник</h1>
          <p>Миры, территории, организации и стандарты контрактной работы.</p>
        </div>

        <label className={styles.search}>
          <Search size={18} aria-hidden="true" />
          <input
            type="search"
            value={search}
            placeholder={`Поиск: ${activeDefinition.label.toLocaleLowerCase("ru-RU")}`}
            onChange={(event) => setSearch(event.target.value)}
          />
        </label>
      </header>

      <nav className={styles.tabs} aria-label="Разделы справочника">
        {sections.map(({ icon: Icon, id, label }) => (
          <button
            className={activeSection === id ? styles.active : ""}
            key={id}
            type="button"
            onClick={() => {
              setActiveSection(id);
              setSearchParams({ section: id }, { replace: true });
              setSearch("");
            }}
          >
            <Icon size={17} aria-hidden="true" />
            <span>{label}</span>
            <b>{dictionaries[id]?.length ?? 0}</b>
          </button>
        ))}
      </nav>

      {dictionaryQuery.isError ? (
        <div className="notice error">{getApiErrorMessage(dictionaryQuery.error)}</div>
      ) : null}

      <section className={styles.content} aria-live="polite">
        <header className={styles.contentHeader}>
          <div>
            <ActiveSectionIcon size={20} aria-hidden="true" />
            <h2>{activeDefinition.label}</h2>
          </div>
          <span>{visibleItems.length} записей</span>
        </header>

        {dictionaryQuery.isLoading ? (
          <div className={styles.loadingGrid} aria-label="Загрузка справочника">
            {Array.from({ length: 6 }, (_, index) => (
              <span key={index} />
            ))}
          </div>
        ) : visibleItems.length > 0 ? (
          <div className={styles.grid}>
            {visibleItems.map((item) => (
              <DirectoryCard
                item={item}
                key={item.id ?? item.code}
                relations={relations}
                type={activeSection}
              />
            ))}
          </div>
        ) : (
          <EmptyState title="Ничего не найдено" text="Попробуйте изменить поисковый запрос." />
        )}
      </section>
    </div>
  );
}
