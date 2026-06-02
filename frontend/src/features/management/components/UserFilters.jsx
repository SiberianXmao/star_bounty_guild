import { Search } from "lucide-react";
import { roleLabel, statusLabel } from "../labels.js";
import { updateFormValue } from "../utils.js";
import styles from "../ManagementConsole.module.css";

export default function UserFilters({ filters, roles, setFilters, statuses }) {
  return (
    <div className={styles.filters}>
      <label className="field">
        <span>Поиск</span>
        <div className={styles.searchField}>
          <Search size={17} aria-hidden="true" />
          <input
            className="input"
            name="q"
            placeholder="email, username, имя"
            value={filters.q}
            onChange={(event) => updateFormValue(setFilters, event)}
          />
        </div>
      </label>
      <label className="field">
        <span>Статус</span>
        <select
          className="select"
          name="status"
          value={filters.status}
          onChange={(event) => updateFormValue(setFilters, event)}
        >
          <option value="">Любой</option>
          {statuses.map((status) => (
            <option key={status} value={status}>
              {statusLabel(status)}
            </option>
          ))}
        </select>
      </label>
      <label className="field">
        <span>Роль</span>
        <select
          className="select"
          name="role"
          value={filters.role}
          onChange={(event) => updateFormValue(setFilters, event)}
        >
          <option value="">Любая</option>
          {roles.map((role) => (
            <option key={role} value={role}>
              {roleLabel(role)}
            </option>
          ))}
        </select>
      </label>
    </div>
  );
}
