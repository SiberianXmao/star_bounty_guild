import { ArchiveX, BadgePlus, Ban, Check, Trash2, UsersRound, X } from "lucide-react";
import EmptyState from "../../../components/ui/EmptyState.jsx";
import StatusBadge from "../../../components/ui/StatusBadge.jsx";
import { getApiErrorMessage } from "../../../services/apiClient.js";
import { moderatorStatusOptions, statusOptions } from "../constants.js";
import { roleLabel, statusLabel, toneForUserStatus } from "../labels.js";
import { isStaffUser } from "../utils.js";
import styles from "../ManagementConsole.module.css";

export default function UserTable({
  deleteMutation,
  mode,
  roleDrafts,
  roleMutation,
  roles,
  setRoleDrafts,
  statusMutation,
  users,
}) {
  if (!users.length) {
    return <EmptyState icon={UsersRound} title="Пользователи не найдены" />;
  }

  const isAdminMode = mode === "admin";
  const availableStatuses = isAdminMode ? statusOptions : moderatorStatusOptions;

  return (
    <div className={styles.userList}>
      {users.map((item) => {
        const selectedRole = roleDrafts[item.id] ?? roles[0] ?? "CLIENT";
        const rowClassName = `${styles.userRow} ${!isAdminMode ? styles.moderatorRow : ""}`;
        const canModerate = isAdminMode || !isStaffUser(item);

        return (
          <article className={rowClassName} key={item.id}>
            <div className={styles.userMain}>
              <strong>{item.displayName || item.username}</strong>
              <span>{item.email}</span>
              <small>{item.username}</small>
            </div>
            <StatusBadge tone={toneForUserStatus(item.status)}>
              {statusLabel(item.status)}
            </StatusBadge>
            <div className={styles.roleStack}>
              {(item.roles ?? []).map((role) =>
                isAdminMode ? (
                  <button
                    className={styles.roleChip}
                    key={role}
                    title="Снять роль"
                    type="button"
                    onClick={() =>
                      roleMutation.mutate({
                        action: "remove",
                        roleName: role,
                        userId: item.id,
                      })
                    }
                  >
                    {roleLabel(role)}
                    <X size={13} aria-hidden="true" />
                  </button>
                ) : (
                  <span className={styles.roleChip} key={role}>
                    {roleLabel(role)}
                  </span>
                ),
              )}
            </div>
            {isAdminMode ? (
              <div className={styles.roleAssign}>
                <select
                  className="select"
                  value={selectedRole}
                  onChange={(event) =>
                    setRoleDrafts((current) => ({
                      ...current,
                      [item.id]: event.target.value,
                    }))
                  }
                >
                  {roles.map((role) => (
                    <option key={role} value={role}>
                      {roleLabel(role)}
                    </option>
                  ))}
                </select>
                <button
                  className="iconButton"
                  title="Выдать роль"
                  type="button"
                  onClick={() =>
                    roleMutation.mutate({
                      action: "assign",
                      roleName: selectedRole,
                      userId: item.id,
                    })
                  }
                >
                  <BadgePlus size={17} aria-hidden="true" />
                </button>
              </div>
            ) : null}
            <div className={styles.statusActions}>
              {availableStatuses.map((status) => (
                <button
                  className="iconButton"
                  disabled={!canModerate || item.status === status}
                  key={status}
                  title={statusActionLabel(status)}
                  type="button"
                  onClick={() => statusMutation.mutate({ status, userId: item.id })}
                >
                  {statusIcon(status)}
                </button>
              ))}
              {isAdminMode && item.status === "DELETED" ? (
                <button
                  className="iconButton"
                  disabled={deleteMutation?.isPending}
                  title="Удалить из БД"
                  type="button"
                  onClick={() => {
                    const confirmed = window.confirm(
                      "Удалить пользователя из БД навсегда? Это сработает только для аккаунтов без профилей и истории.",
                    );

                    if (confirmed) {
                      deleteMutation.mutate(item.id);
                    }
                  }}
                >
                  <Trash2 size={17} aria-hidden="true" />
                </button>
              ) : null}
            </div>
          </article>
        );
      })}
      {roleMutation?.isError ? <div className="notice error">{getApiErrorMessage(roleMutation.error)}</div> : null}
      {statusMutation.isError ? <div className="notice error">{getApiErrorMessage(statusMutation.error)}</div> : null}
      {deleteMutation?.isError ? <div className="notice error">{getApiErrorMessage(deleteMutation.error)}</div> : null}
    </div>
  );
}

function statusActionLabel(status) {
  return {
    ACTIVE: "Активировать",
    BLOCKED: "Заблокировать",
    DELETED: "Перенести в архив",
  }[status] ?? status;
}

function statusIcon(status) {
  if (status === "ACTIVE") {
    return <Check size={17} aria-hidden="true" />;
  }

  if (status === "BLOCKED") {
    return <Ban size={17} aria-hidden="true" />;
  }

  return <ArchiveX size={17} aria-hidden="true" />;
}
