import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { ChevronLeft, ChevronRight, ListFilter, RefreshCw, Search } from "lucide-react";
import EmptyState from "../components/EmptyState.jsx";
import OrderCard from "../components/OrderCard.jsx";
import { dictionaryApi, ordersApi } from "../services/bountyApi.js";
import { getApiErrorMessage } from "../services/apiClient.js";
import { compactParams } from "../utils/formatters.js";
import {
  ACCEPTANCE_OPTIONS,
  RISK_OPTIONS,
  URGENCY_OPTIONS,
} from "../utils/labels.js";
import { useDebouncedValue } from "../hooks/useDebouncedValue.js";
import heroImage from "../assets/guild-operations-deck.png";
import styles from "./BoardPage.module.css";

const defaultFilters = {
  q: "",
  categoryId: "",
  planetId: "",
  sectorId: "",
  riskLevel: "",
  urgencyLevel: "",
  acceptanceMode: "",
  rewardMin: "",
  rewardMax: "",
  sort: "publishedAt,desc",
};

const sortOptions = [
  { value: "publishedAt,desc", label: "Новые" },
  { value: "rewardAmount,desc", label: "Награда" },
  { value: "deadline,asc", label: "Дедлайн" },
  { value: "createdAt,desc", label: "Создание" },
];

export default function BoardPage() {
  const [filters, setFilters] = useState(defaultFilters);
  const [page, setPage] = useState(0);
  const debouncedSearch = useDebouncedValue(filters.q);

  const dictionariesQuery = useQuery({
    queryKey: ["dictionaries"],
    queryFn: dictionaryApi.all,
  });

  const orderParams = useMemo(
    () =>
      compactParams({
        ...filters,
        q: debouncedSearch,
        page,
        size: 6,
      }),
    [debouncedSearch, filters, page],
  );

  const ordersQuery = useQuery({
    queryKey: ["orders", orderParams],
    queryFn: () => ordersApi.publicBoard(orderParams),
  });

  const dictionaries = dictionariesQuery.data ?? {};
  const orders = ordersQuery.data?.content ?? [];

  const handleFilterChange = (event) => {
    const { name, value } = event.target;

    setPage(0);
    setFilters((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const resetFilters = () => {
    setPage(0);
    setFilters(defaultFilters);
  };

  return (
    <div className={styles.page}>
      <section
        className={styles.hero}
        style={{
          backgroundImage: `linear-gradient(90deg, rgba(16, 14, 12, 0.92), rgba(16, 14, 12, 0.64) 48%, rgba(16, 14, 12, 0.25)), url(${heroImage})`,
        }}
      >
        <div className={styles.heroContent}>
          <span className={styles.kicker}>Guild terminal</span>
          <h1>Доска контрактов</h1>
          <div className={styles.quickStats}>
            <span>
              <b>{ordersQuery.data?.totalElements ?? 0}</b>
              открыто
            </span>
            <span>
              <b>{dictionaries.categories?.length ?? 0}</b>
              категорий
            </span>
            <span>
              <b>{dictionaries.planets?.length ?? 0}</b>
              планет
            </span>
          </div>
        </div>
      </section>

      <section className={styles.workspace}>
        <aside className={styles.filters}>
          <div className={styles.panelHeader}>
            <ListFilter size={18} aria-hidden="true" />
            <h2>Фильтры</h2>
          </div>

          <label className="field">
            <span>Поиск</span>
            <div className={styles.searchField}>
              <Search size={17} aria-hidden="true" />
              <input
                className="input"
                name="q"
                value={filters.q}
                onChange={handleFilterChange}
                placeholder="название, описание"
              />
            </div>
          </label>

          <label className="field">
            <span>Категория</span>
            <select
              className="select"
              name="categoryId"
              value={filters.categoryId}
              onChange={handleFilterChange}
            >
              <option value="">Все</option>
              {dictionaries.categories?.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>Планета</span>
            <select
              className="select"
              name="planetId"
              value={filters.planetId}
              onChange={handleFilterChange}
            >
              <option value="">Любая</option>
              {dictionaries.planets?.map((planet) => (
                <option key={planet.id} value={planet.id}>
                  {planet.name}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>Сектор</span>
            <select
              className="select"
              name="sectorId"
              value={filters.sectorId}
              onChange={handleFilterChange}
            >
              <option value="">Любой</option>
              {dictionaries.sectors?.map((sector) => (
                <option key={sector.id} value={sector.id}>
                  {sector.name}
                </option>
              ))}
            </select>
          </label>

          <div className={styles.double}>
            <label className="field">
              <span>Риск</span>
              <select
                className="select"
                name="riskLevel"
                value={filters.riskLevel}
                onChange={handleFilterChange}
              >
                <option value="">Все</option>
                {RISK_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>Срочность</span>
              <select
                className="select"
                name="urgencyLevel"
                value={filters.urgencyLevel}
                onChange={handleFilterChange}
              >
                <option value="">Все</option>
                {URGENCY_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <label className="field">
            <span>Принятие</span>
            <select
              className="select"
              name="acceptanceMode"
              value={filters.acceptanceMode}
              onChange={handleFilterChange}
            >
              <option value="">Любое</option>
              {ACCEPTANCE_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>

          <div className={styles.double}>
            <label className="field">
              <span>Мин.</span>
              <input
                className="input"
                name="rewardMin"
                type="number"
                min="0"
                value={filters.rewardMin}
                onChange={handleFilterChange}
              />
            </label>
            <label className="field">
              <span>Макс.</span>
              <input
                className="input"
                name="rewardMax"
                type="number"
                min="0"
                value={filters.rewardMax}
                onChange={handleFilterChange}
              />
            </label>
          </div>

          <label className="field">
            <span>Сортировка</span>
            <select
              className="select"
              name="sort"
              value={filters.sort}
              onChange={handleFilterChange}
            >
              {sortOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>

          <button className="button ghost" type="button" onClick={resetFilters}>
            <RefreshCw size={17} aria-hidden="true" />
            Сбросить
          </button>
        </aside>

        <section className={styles.board}>
          <div className={styles.boardHeader}>
            <div>
              <span>{ordersQuery.isFetching ? "Сканирование" : "Найдено"}</span>
              <strong>{ordersQuery.data?.totalElements ?? 0}</strong>
            </div>
            <div className={styles.pagination}>
              <button
                className="iconButton"
                type="button"
                onClick={() => setPage((current) => Math.max(current - 1, 0))}
                disabled={ordersQuery.data?.first || ordersQuery.isLoading}
                aria-label="Предыдущая страница"
              >
                <ChevronLeft size={18} aria-hidden="true" />
              </button>
              <span>
                {(ordersQuery.data?.page ?? 0) + 1} / {ordersQuery.data?.totalPages || 1}
              </span>
              <button
                className="iconButton"
                type="button"
                onClick={() => setPage((current) => current + 1)}
                disabled={ordersQuery.data?.last || ordersQuery.isLoading}
                aria-label="Следующая страница"
              >
                <ChevronRight size={18} aria-hidden="true" />
              </button>
            </div>
          </div>

          {ordersQuery.isError ? (
            <div className="notice error">{getApiErrorMessage(ordersQuery.error)}</div>
          ) : null}

          {ordersQuery.isLoading ? (
            <div className={styles.grid}>
              {Array.from({ length: 6 }).map((_, index) => (
                <div className={styles.skeleton} key={index} />
              ))}
            </div>
          ) : orders.length > 0 ? (
            <div className={styles.grid}>
              {orders.map((order) => (
                <OrderCard key={order.id} order={order} />
              ))}
            </div>
          ) : (
            <EmptyState title="Контракты не найдены" text="Измени фильтры или проверь backend." />
          )}
        </section>
      </section>
    </div>
  );
}
