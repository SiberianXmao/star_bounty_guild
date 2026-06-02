import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Database, UserPlus, UsersRound } from "lucide-react";
import { useAuth } from "../../context/AuthContext.jsx";
import { adminApi, dictionaryApi, moderationApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import { normalizeOptionalString } from "../../utils/formatters.js";
import {
  dictionaryDefaults,
  roleOptions,
  statusOptions,
  userDefaults,
} from "./constants.js";
import { uniqueRolesFromUsers } from "./utils.js";
import AccessGate from "./components/AccessGate.jsx";
import CatalogExplorer from "./components/CatalogExplorer.jsx";
import CreateUserForm from "./components/CreateUserForm.jsx";
import DictionaryForms from "./components/DictionaryForms.jsx";
import ManagementHeader from "./components/ManagementHeader.jsx";
import ManagementTabs from "./components/ManagementTabs.jsx";
import PanelTitle from "./components/PanelTitle.jsx";
import UserFilters from "./components/UserFilters.jsx";
import UserTable from "./components/UserTable.jsx";
import styles from "./ManagementConsole.module.css";

export default function ManagementConsole({ mode }) {
  const queryClient = useQueryClient();
  const { isAuthenticated, user } = useAuth();
  const [activeTab, setActiveTab] = useState("users");
  const [userForm, setUserForm] = useState(userDefaults);
  const [dictionaryForms, setDictionaryForms] = useState(dictionaryDefaults);
  const [roleDrafts, setRoleDrafts] = useState({});
  const [filters, setFilters] = useState({ q: "", status: "", role: "" });

  const isAdminMode = mode === "admin";
  const userRoles = user?.roles ?? [];
  const isAllowed = isAdminMode
    ? userRoles.includes("ADMIN")
    : userRoles.includes("ADMIN") || userRoles.includes("MODERATOR");

  const usersQuery = useQuery({
    queryKey: [mode, "users"],
    queryFn: isAdminMode ? adminApi.users : moderationApi.users,
    enabled: Boolean(isAllowed),
  });

  const rolesQuery = useQuery({
    queryKey: ["admin", "roles"],
    queryFn: adminApi.roles,
    enabled: Boolean(isAllowed && isAdminMode),
  });

  const dictionariesQuery = useQuery({
    queryKey: ["dictionaries"],
    queryFn: dictionaryApi.all,
    enabled: Boolean(isAllowed),
  });

  const users = usersQuery.data ?? [];
  const roles = useMemo(() => {
    if (isAdminMode) {
      return rolesQuery.data?.map((role) => role.name) ?? roleOptions;
    }

    const userRolesForFilter = uniqueRolesFromUsers(users);
    return userRolesForFilter.length ? userRolesForFilter : roleOptions;
  }, [isAdminMode, rolesQuery.data, users]);
  const dictionaries = dictionariesQuery.data ?? {};

  const filteredUsers = useMemo(() => {
    const search = filters.q.trim().toLowerCase();

    return users.filter((item) => {
      const matchesText =
        !search ||
        item.email?.toLowerCase().includes(search) ||
        item.username?.toLowerCase().includes(search) ||
        item.displayName?.toLowerCase().includes(search);

      const matchesStatus = !filters.status || item.status === filters.status;
      const matchesRole = !filters.role || item.roles?.includes(filters.role);

      return matchesText && matchesStatus && matchesRole;
    });
  }, [filters, users]);

  const createUserMutation = useMutation({
    mutationFn: async () => {
      const createdUser = await adminApi.createUser({
        email: userForm.email,
        username: userForm.username,
        displayName: normalizeOptionalString(userForm.displayName),
        avatarUrl: null,
        password: userForm.password,
      });

      if (userForm.roleName) {
        return adminApi.assignRole(createdUser.id, userForm.roleName);
      }

      return createdUser;
    },
    onSuccess: () => {
      setUserForm(userDefaults);
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });

  const statusMutation = useMutation({
    mutationFn: ({ userId, status }) =>
      isAdminMode
        ? adminApi.updateUserStatus(userId, status)
        : moderationApi.updateUserStatus(userId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [mode, "users"] });
    },
  });

  const roleMutation = useMutation({
    mutationFn: ({ action, userId, roleName }) =>
      action === "assign"
        ? adminApi.assignRole(userId, roleName)
        : adminApi.removeRole(userId, roleName),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });

  const permanentDeleteMutation = useMutation({
    mutationFn: adminApi.deleteUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });

  const dictionaryMutation = useMutation({
    mutationFn: ({ type, payload }) => {
      const actions = {
        faction: dictionaryApi.createFaction,
        sector: dictionaryApi.createSector,
        planet: dictionaryApi.createPlanet,
        category: dictionaryApi.createCategory,
        currency: dictionaryApi.createCurrency,
        skill: dictionaryApi.createSkill,
      };

      return actions[type](payload);
    },
    onSuccess: (_data, variables) => {
      setDictionaryForms((current) => ({
        ...current,
        [variables.type]: dictionaryDefaults[variables.type],
      }));
      queryClient.invalidateQueries({ queryKey: ["dictionaries"] });
    },
  });

  if (!isAllowed) {
    return (
      <AccessGate
        isAuthenticated={isAuthenticated}
        title={isAdminMode ? "Админ-панель" : "Панель модератора"}
      />
    );
  }

  return (
    <div className={styles.page}>
      <ManagementHeader dictionaries={dictionaries} mode={mode} users={users} />
      <ManagementTabs activeTab={activeTab} setActiveTab={setActiveTab} />

      {activeTab === "users" ? (
        <section className={`${styles.workspace} ${!isAdminMode ? styles.singleWorkspace : ""}`}>
          {isAdminMode ? (
            <div className={styles.sidePanel}>
              <PanelTitle icon={UserPlus} title="Новый пользователь" />
              <CreateUserForm
                form={userForm}
                mutation={createUserMutation}
                roles={roles}
                setForm={setUserForm}
              />
            </div>
          ) : null}

          <div className={styles.mainPanel}>
            <PanelTitle icon={UsersRound} title="Пользователи гильдии" />
            <UserFilters
              filters={filters}
              roles={roles}
              setFilters={setFilters}
              statuses={statusOptions}
            />
            {usersQuery.isError ? (
              <div className="notice error">{getApiErrorMessage(usersQuery.error)}</div>
            ) : null}
            {usersQuery.isLoading ? (
              <div className={styles.loading}>Загрузка реестра...</div>
            ) : (
              <UserTable
                deleteMutation={permanentDeleteMutation}
                mode={mode}
                roleDrafts={roleDrafts}
                roleMutation={roleMutation}
                roles={roles}
                setRoleDrafts={setRoleDrafts}
                statusMutation={statusMutation}
                users={filteredUsers}
              />
            )}
          </div>
        </section>
      ) : null}

      {activeTab === "dictionary" ? (
        <section className={styles.dictionaryLayout}>
          <CatalogExplorer
            dictionaries={dictionaries}
            error={dictionariesQuery.error}
            isError={dictionariesQuery.isError}
            isLoading={dictionariesQuery.isLoading}
          />
          <div className={styles.catalogPanel}>
            <PanelTitle icon={Database} title="Пополнение справочников" />
            <DictionaryForms
              dictionaries={dictionaries}
              dictionaryForms={dictionaryForms}
              dictionaryMutation={dictionaryMutation}
              setDictionaryForms={setDictionaryForms}
            />
          </div>
        </section>
      ) : null}
    </div>
  );
}
