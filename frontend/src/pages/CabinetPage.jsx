import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  BadgePlus,
  Check,
  ClipboardList,
  Play,
  Plus,
  Send,
  Shield,
  Trash2,
  UserRound,
  X,
} from "lucide-react";
import EmptyState from "../components/EmptyState.jsx";
import StatusBadge from "../components/StatusBadge.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import {
  applicationsApi,
  dictionaryApi,
  ordersApi,
  profilesApi,
} from "../services/bountyApi.js";
import { getApiErrorMessage } from "../services/apiClient.js";
import {
  dateTimeLocalToIso,
  formatDate,
  formatReward,
  normalizeOptionalString,
  shortId,
} from "../utils/formatters.js";
import {
  ACCEPTANCE_OPTIONS,
  APPLICATION_STATUS_OPTIONS,
  AVAILABILITY_OPTIONS,
  RISK_OPTIONS,
  URGENCY_OPTIONS,
  VISIBILITY_OPTIONS,
  applicationStatusLabel,
  statusLabel,
  toneForStatus,
} from "../utils/labels.js";
import styles from "./CabinetPage.module.css";

const clientProfileDefaults = {
  name: "",
  description: "",
  factionId: "",
  planetId: "",
};

const hunterProfileDefaults = {
  callsign: "",
  bio: "",
  factionId: "",
  homePlanetId: "",
  availabilityStatus: "AVAILABLE",
  minReward: "",
};

const skillDefaults = {
  skillId: "",
  level: 50,
};

const orderDefaults = {
  title: "",
  description: "",
  categoryId: "",
  rewardAmount: "",
  rewardCurrencyCode: "",
  planetId: "",
  sectorId: "",
  riskLevel: "MEDIUM",
  urgencyLevel: "NORMAL",
  visibility: "PUBLIC",
  acceptanceMode: "APPLICATIONS",
  requirements: "",
  deadline: "",
  publishNow: true,
};

const tabs = [
  { id: "overview", label: "Сводка" },
  { id: "client", label: "Заказчик" },
  { id: "hunter", label: "Охотник" },
];

export default function CabinetPage() {
  const queryClient = useQueryClient();
  const { isAuthenticated, user } = useAuth();
  const [activeTab, setActiveTab] = useState("overview");
  const [expandedOrderId, setExpandedOrderId] = useState("");
  const [clientForm, setClientForm] = useState(clientProfileDefaults);
  const [hunterForm, setHunterForm] = useState(hunterProfileDefaults);
  const [skillForm, setSkillForm] = useState(skillDefaults);
  const [orderForm, setOrderForm] = useState(orderDefaults);

  const dictionariesQuery = useQuery({
    queryKey: ["dictionaries"],
    queryFn: dictionaryApi.all,
    enabled: isAuthenticated,
  });

  const profilesQuery = useQuery({
    queryKey: ["profiles", "me"],
    queryFn: profilesApi.me,
    enabled: isAuthenticated,
  });

  const clientProfile = profilesQuery.data?.clientProfile;
  const hunterProfile = profilesQuery.data?.hunterProfile;

  const clientOrdersQuery = useQuery({
    queryKey: ["orders", "my-client"],
    queryFn: () => ordersApi.myClient({ size: 20, sort: "createdAt,desc" }),
    enabled: Boolean(clientProfile),
  });

  const hunterOrdersQuery = useQuery({
    queryKey: ["orders", "my-hunter"],
    queryFn: () => ordersApi.myHunter({ size: 20, sort: "createdAt,desc" }),
    enabled: Boolean(hunterProfile),
  });

  const myApplicationsQuery = useQuery({
    queryKey: ["applications", "my-hunter"],
    queryFn: applicationsApi.myHunter,
    enabled: Boolean(hunterProfile),
  });

  const dictionaries = dictionariesQuery.data ?? {};

  const createClientMutation = useMutation({
    mutationFn: () =>
      profilesApi.createClient({
        name: clientForm.name,
        description: normalizeOptionalString(clientForm.description),
        factionId: clientForm.factionId || null,
        planetId: clientForm.planetId || null,
      }),
    onSuccess: () => {
      setClientForm(clientProfileDefaults);
      queryClient.invalidateQueries({ queryKey: ["profiles"] });
    },
  });

  const createHunterMutation = useMutation({
    mutationFn: () =>
      profilesApi.createHunter({
        callsign: hunterForm.callsign,
        bio: normalizeOptionalString(hunterForm.bio),
        factionId: hunterForm.factionId || null,
        homePlanetId: hunterForm.homePlanetId || null,
        availabilityStatus: hunterForm.availabilityStatus,
        minReward: hunterForm.minReward || null,
      }),
    onSuccess: () => {
      setHunterForm(hunterProfileDefaults);
      queryClient.invalidateQueries({ queryKey: ["profiles"] });
    },
  });

  const addSkillMutation = useMutation({
    mutationFn: () =>
      profilesApi.addHunterSkill({
        skillId: skillForm.skillId,
        level: Number(skillForm.level),
      }),
    onSuccess: () => {
      setSkillForm(skillDefaults);
      queryClient.invalidateQueries({ queryKey: ["profiles"] });
      queryClient.invalidateQueries({ queryKey: ["hunters"] });
    },
  });

  const createOrderMutation = useMutation({
    mutationFn: async () => {
      const draft = await ordersApi.createDraft({
        title: orderForm.title,
        description: orderForm.description,
        categoryId: orderForm.categoryId,
        rewardAmount: orderForm.rewardAmount,
        rewardCurrencyCode: orderForm.rewardCurrencyCode,
        planetId: orderForm.planetId || null,
        sectorId: orderForm.sectorId || null,
        riskLevel: orderForm.riskLevel,
        urgencyLevel: orderForm.urgencyLevel,
        visibility: orderForm.visibility,
        acceptanceMode: orderForm.acceptanceMode,
        requirements: normalizeOptionalString(orderForm.requirements),
        deadline: dateTimeLocalToIso(orderForm.deadline),
      });

      return orderForm.publishNow ? ordersApi.publish(draft.id) : draft;
    },
    onSuccess: () => {
      setOrderForm(orderDefaults);
      queryClient.invalidateQueries({ queryKey: ["orders"] });
    },
  });

  const orderActionMutation = useMutation({
    mutationFn: ({ action, orderId }) => {
      const actions = {
        publish: ordersApi.publish,
        cancel: ordersApi.cancel,
        start: ordersApi.start,
        submit: ordersApi.submit,
        complete: ordersApi.complete,
      };

      return actions[action](orderId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
      queryClient.invalidateQueries({ queryKey: ["applications"] });
    },
  });

  const applicationActionMutation = useMutation({
    mutationFn: ({ action, applicationId }) => {
      const actions = {
        accept: applicationsApi.accept,
        reject: applicationsApi.reject,
        withdraw: applicationsApi.withdraw,
      };

      return actions[action](applicationId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["applications"] });
      queryClient.invalidateQueries({ queryKey: ["orders"] });
    },
  });

  if (!isAuthenticated) {
    return (
      <section className={styles.authGate}>
        <h1>Кабинет</h1>
        <p>Войдите через панель в верхней части экрана.</p>
      </section>
    );
  }

  const clientOrders = clientOrdersQuery.data?.content ?? [];
  const hunterOrders = hunterOrdersQuery.data?.content ?? [];
  const myApplications = myApplicationsQuery.data ?? [];

  return (
    <div className={styles.page}>
      <section className={styles.header}>
        <div>
          <span className={styles.kicker}>Guild account</span>
          <h1>{user?.displayName || user?.username}</h1>
        </div>
        <div className={styles.roleBadges}>
          {user?.roles?.map((role) => (
            <StatusBadge key={role} tone="info">
              {role}
            </StatusBadge>
          ))}
        </div>
      </section>

      <nav className={styles.tabs} aria-label="Разделы кабинета">
        {tabs.map((tab) => (
          <button
            className={activeTab === tab.id ? styles.active : ""}
            key={tab.id}
            type="button"
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      {profilesQuery.isError ? (
        <div className="notice error">{getApiErrorMessage(profilesQuery.error)}</div>
      ) : null}

      {activeTab === "overview" ? (
        <section className={styles.overview}>
          <ProfileCard
            icon={UserRound}
            title="Профиль заказчика"
            profile={clientProfile}
            empty="Не создан"
            lines={[
              clientProfile?.factionName,
              clientProfile?.planetName,
              `Надежность: ${clientProfile?.reliabilityScore ?? 0}`,
            ]}
          />
          <ProfileCard
            icon={Shield}
            title="Профиль охотника"
            profile={hunterProfile}
            empty="Не создан"
            lines={[
              hunterProfile?.factionName,
              hunterProfile?.homePlanetName,
              `Надежность: ${hunterProfile?.reliabilityScore ?? 0}`,
            ]}
          />
          <MetricCard label="Мои заказы" value={clientOrders.length} />
          <MetricCard label="Назначено мне" value={hunterOrders.length} />
          <MetricCard label="Мои отклики" value={myApplications.length} />
        </section>
      ) : null}

      {activeTab === "client" ? (
        <section className={styles.columns}>
          <div className={styles.panel}>
            <PanelTitle icon={UserRound} title="Профиль заказчика" />
            {clientProfile ? (
              <ProfileSummary
                title={clientProfile.name}
                subtitle={clientProfile.description}
                rows={[
                  ["Фракция", clientProfile.factionName || "Не указана"],
                  ["Планета", clientProfile.planetName || "Не указана"],
                  ["Рейтинг", clientProfile.averageRating ?? "0.0"],
                  ["Надежность", clientProfile.reliabilityScore ?? 0],
                ]}
              />
            ) : (
              <ClientProfileForm
                dictionaries={dictionaries}
                form={clientForm}
                mutation={createClientMutation}
                setForm={setClientForm}
              />
            )}
          </div>

          <div className={styles.panel}>
            <PanelTitle icon={ClipboardList} title="Новый контракт" />
            {clientProfile ? (
              <OrderForm
                dictionaries={dictionaries}
                form={orderForm}
                mutation={createOrderMutation}
                setForm={setOrderForm}
              />
            ) : (
              <EmptyState title="Нужен профиль заказчика" />
            )}
          </div>

          <div className={styles.widePanel}>
            <PanelTitle icon={ClipboardList} title="Мои контракты" />
            <OrderList
              applicationsOrderId={expandedOrderId}
              applicationsMutation={applicationActionMutation}
              emptyTitle="Контрактов пока нет"
              onAction={(action, orderId) => orderActionMutation.mutate({ action, orderId })}
              onToggleApplications={(orderId) =>
                setExpandedOrderId((current) => (current === orderId ? "" : orderId))
              }
              orders={clientOrders}
              role="client"
            />
            {orderActionMutation.isError ? (
              <div className="notice error">{getApiErrorMessage(orderActionMutation.error)}</div>
            ) : null}
          </div>
        </section>
      ) : null}

      {activeTab === "hunter" ? (
        <section className={styles.columns}>
          <div className={styles.panel}>
            <PanelTitle icon={Shield} title="Профиль охотника" />
            {hunterProfile ? (
              <ProfileSummary
                title={hunterProfile.callsign}
                subtitle={hunterProfile.bio}
                rows={[
                  ["Фракция", hunterProfile.factionName || "Не указана"],
                  ["Планета", hunterProfile.homePlanetName || "Не указана"],
                  ["Рейтинг", hunterProfile.averageRating ?? "0.0"],
                  ["Надежность", hunterProfile.reliabilityScore ?? 0],
                ]}
              />
            ) : (
              <HunterProfileForm
                dictionaries={dictionaries}
                form={hunterForm}
                mutation={createHunterMutation}
                setForm={setHunterForm}
              />
            )}
          </div>

          <div className={styles.panel}>
            <PanelTitle icon={BadgePlus} title="Навыки" />
            {hunterProfile ? (
              <SkillForm
                dictionaries={dictionaries}
                form={skillForm}
                hunterProfile={hunterProfile}
                mutation={addSkillMutation}
                setForm={setSkillForm}
              />
            ) : (
              <EmptyState title="Нужен профиль охотника" />
            )}
          </div>

          <div className={styles.widePanel}>
            <PanelTitle icon={ClipboardList} title="Назначенные контракты" />
            <OrderList
              emptyTitle="Назначений пока нет"
              onAction={(action, orderId) => orderActionMutation.mutate({ action, orderId })}
              orders={hunterOrders}
              role="hunter"
            />
          </div>

          <div className={styles.widePanel}>
            <PanelTitle icon={Send} title="Мои отклики" />
            <ApplicationsList
              applications={myApplications}
              mutation={applicationActionMutation}
              role="hunter"
            />
          </div>
        </section>
      ) : null}
    </div>
  );
}

function PanelTitle({ icon: Icon, title }) {
  return (
    <div className={styles.panelTitle}>
      <Icon size={18} aria-hidden="true" />
      <h2>{title}</h2>
    </div>
  );
}

function ProfileCard({ empty, icon: Icon, lines, profile, title }) {
  return (
    <article className={styles.profileCard}>
      <Icon size={22} aria-hidden="true" />
      <span>{title}</span>
      <strong>{profile?.name || profile?.callsign || empty}</strong>
      {profile ? (
        <ul>
          {lines.filter(Boolean).map((line) => (
            <li key={line}>{line}</li>
          ))}
        </ul>
      ) : null}
    </article>
  );
}

function MetricCard({ label, value }) {
  return (
    <article className={styles.metricCard}>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function ProfileSummary({ rows, subtitle, title }) {
  return (
    <div className={styles.profileSummary}>
      <h3>{title}</h3>
      {subtitle ? <p>{subtitle}</p> : null}
      <dl>
        {rows.map(([label, value]) => (
          <div key={label}>
            <dt>{label}</dt>
            <dd>{value}</dd>
          </div>
        ))}
      </dl>
    </div>
  );
}

function ClientProfileForm({ dictionaries, form, mutation, setForm }) {
  return (
    <FormShell
      error={mutation.error}
      isPending={mutation.isPending}
      onSubmit={() => mutation.mutate()}
      submitIcon={Plus}
      submitLabel="Создать профиль"
    >
      <InputField form={form} label="Имя" name="name" required setForm={setForm} />
      <TextAreaField form={form} label="Описание" name="description" setForm={setForm} />
      <SelectField
        form={form}
        label="Фракция"
        name="factionId"
        options={dictionaries.factions ?? []}
        setForm={setForm}
      />
      <SelectField
        form={form}
        label="Планета"
        name="planetId"
        options={dictionaries.planets ?? []}
        setForm={setForm}
      />
    </FormShell>
  );
}

function HunterProfileForm({ dictionaries, form, mutation, setForm }) {
  return (
    <FormShell
      error={mutation.error}
      isPending={mutation.isPending}
      onSubmit={() => mutation.mutate()}
      submitIcon={Plus}
      submitLabel="Создать профиль"
    >
      <InputField form={form} label="Позывной" name="callsign" required setForm={setForm} />
      <TextAreaField form={form} label="Биография" name="bio" setForm={setForm} />
      <SelectField
        form={form}
        label="Доступность"
        name="availabilityStatus"
        options={AVAILABILITY_OPTIONS}
        setForm={setForm}
        valueKey="value"
      />
      <InputField
        form={form}
        label="Мин. награда"
        min="0"
        name="minReward"
        setForm={setForm}
        type="number"
      />
      <SelectField
        form={form}
        label="Фракция"
        name="factionId"
        options={dictionaries.factions ?? []}
        setForm={setForm}
      />
      <SelectField
        form={form}
        label="Планета"
        name="homePlanetId"
        options={dictionaries.planets ?? []}
        setForm={setForm}
      />
    </FormShell>
  );
}

function SkillForm({ dictionaries, form, hunterProfile, mutation, setForm }) {
  return (
    <div className="stack">
      <div className={styles.skillCloud}>
        {(hunterProfile.skills ?? []).map((skill) => (
          <span key={skill.skillId}>
            {skill.skillName}
            <b>{skill.level}</b>
          </span>
        ))}
      </div>
      <FormShell
        error={mutation.error}
        isPending={mutation.isPending}
        onSubmit={() => mutation.mutate()}
        submitIcon={BadgePlus}
        submitLabel="Добавить"
      >
        <SelectField
          form={form}
          label="Навык"
          name="skillId"
          options={dictionaries.skills ?? []}
          required
          setForm={setForm}
        />
        <InputField
          form={form}
          label="Уровень"
          max="100"
          min="1"
          name="level"
          required
          setForm={setForm}
          type="number"
        />
      </FormShell>
    </div>
  );
}

function OrderForm({ dictionaries, form, mutation, setForm }) {
  const selectedPlanet = dictionaries.planets?.find((planet) => planet.id === form.planetId);

  return (
    <FormShell
      error={mutation.error}
      isPending={mutation.isPending}
      onSubmit={() => mutation.mutate()}
      submitIcon={ClipboardList}
      submitLabel={form.publishNow ? "Создать и опубликовать" : "Создать черновик"}
    >
      <InputField form={form} label="Название" name="title" required setForm={setForm} />
      <TextAreaField form={form} label="Описание" name="description" required setForm={setForm} />
      <SelectField
        form={form}
        label="Категория"
        name="categoryId"
        options={dictionaries.categories ?? []}
        required
        setForm={setForm}
      />
      <div className={styles.twoFields}>
        <InputField
          form={form}
          label="Награда"
          min="1"
          name="rewardAmount"
          required
          setForm={setForm}
          type="number"
        />
        <SelectField
          form={form}
          label="Валюта"
          name="rewardCurrencyCode"
          options={dictionaries.currencies ?? []}
          required
          setForm={setForm}
          valueKey="code"
        />
      </div>
      <div className={styles.twoFields}>
        <SelectField
          form={form}
          label="Планета"
          name="planetId"
          options={dictionaries.planets ?? []}
          setForm={(updater) => {
            setForm((current) => {
              const next = typeof updater === "function" ? updater(current) : updater;
              const planet = dictionaries.planets?.find((item) => item.id === next.planetId);
              return {
                ...next,
                sectorId: planet?.sectorId ?? next.sectorId,
              };
            });
          }}
        />
        <SelectField
          form={{ ...form, sectorId: selectedPlanet?.sectorId ?? form.sectorId }}
          label="Сектор"
          name="sectorId"
          options={dictionaries.sectors ?? []}
          setForm={setForm}
        />
      </div>
      <div className={styles.twoFields}>
        <SelectField
          form={form}
          label="Риск"
          name="riskLevel"
          options={RISK_OPTIONS}
          required
          setForm={setForm}
          valueKey="value"
        />
        <SelectField
          form={form}
          label="Срочность"
          name="urgencyLevel"
          options={URGENCY_OPTIONS}
          required
          setForm={setForm}
          valueKey="value"
        />
      </div>
      <div className={styles.twoFields}>
        <SelectField
          form={form}
          label="Видимость"
          name="visibility"
          options={VISIBILITY_OPTIONS}
          required
          setForm={setForm}
          valueKey="value"
        />
        <SelectField
          form={form}
          label="Принятие"
          name="acceptanceMode"
          options={ACCEPTANCE_OPTIONS}
          required
          setForm={setForm}
          valueKey="value"
        />
      </div>
      <TextAreaField form={form} label="Требования" name="requirements" setForm={setForm} />
      <InputField form={form} label="Дедлайн" name="deadline" setForm={setForm} type="datetime-local" />
      <label className={styles.checkbox}>
        <input
          checked={form.publishNow}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              publishNow: event.target.checked,
            }))
          }
          type="checkbox"
        />
        <span>Опубликовать сразу</span>
      </label>
    </FormShell>
  );
}

function OrderList({
  applicationsMutation,
  applicationsOrderId,
  emptyTitle,
  onAction,
  onToggleApplications,
  orders,
  role,
}) {
  if (!orders.length) {
    return <EmptyState title={emptyTitle} />;
  }

  return (
    <div className={styles.orderList}>
      {orders.map((order) => (
        <article className={styles.orderRow} key={order.id}>
          <div className={styles.orderMain}>
            <span>#{shortId(order.id)}</span>
            <h3>{order.title}</h3>
            <p>
              {formatReward(order.rewardAmount, order.rewardCurrencySymbol, order.rewardCurrencyCode)}
              {" · "}
              {order.planetName || order.sectorName || "Локация скрыта"}
              {" · "}
              {formatDate(order.deadline)}
            </p>
          </div>
          <StatusBadge tone={toneForStatus(order.status)}>{statusLabel(order.status)}</StatusBadge>
          <div className={styles.rowActions}>
            {role === "client" && order.status === "DRAFT" ? (
              <button className="button secondary" type="button" onClick={() => onAction("publish", order.id)}>
                <Send size={16} aria-hidden="true" />
                Опубликовать
              </button>
            ) : null}
            {role === "client" && ["DRAFT", "OPEN"].includes(order.status) ? (
              <button className="button danger" type="button" onClick={() => onAction("cancel", order.id)}>
                <X size={16} aria-hidden="true" />
                Отменить
              </button>
            ) : null}
            {role === "client" && order.status === "SUBMITTED" ? (
              <button className="button" type="button" onClick={() => onAction("complete", order.id)}>
                <Check size={16} aria-hidden="true" />
                Завершить
              </button>
            ) : null}
            {role === "client" && order.status === "OPEN" ? (
              <button
                className="button ghost"
                type="button"
                onClick={() => onToggleApplications(order.id)}
              >
                <ClipboardList size={16} aria-hidden="true" />
                Отклики
              </button>
            ) : null}
            {role === "hunter" && order.status === "ASSIGNED" ? (
              <button className="button" type="button" onClick={() => onAction("start", order.id)}>
                <Play size={16} aria-hidden="true" />
                Начать
              </button>
            ) : null}
            {role === "hunter" && order.status === "IN_PROGRESS" ? (
              <button className="button" type="button" onClick={() => onAction("submit", order.id)}>
                <Check size={16} aria-hidden="true" />
                Сдать
              </button>
            ) : null}
          </div>
          {applicationsOrderId === order.id ? (
            <ClientApplicationsPanel
              mutation={applicationsMutation}
              orderId={order.id}
            />
          ) : null}
        </article>
      ))}
    </div>
  );
}

function ClientApplicationsPanel({ mutation, orderId }) {
  const applicationsQuery = useQuery({
    queryKey: ["applications", "client-order", orderId],
    queryFn: () => applicationsApi.forClientOrder(orderId),
  });

  if (applicationsQuery.isLoading) {
    return <div className={styles.inlineNotice}>Загрузка откликов...</div>;
  }

  if (applicationsQuery.isError) {
    return <div className="notice error">{getApiErrorMessage(applicationsQuery.error)}</div>;
  }

  return (
    <ApplicationsList
      applications={applicationsQuery.data ?? []}
      mutation={mutation}
      role="client"
    />
  );
}

function ApplicationsList({ applications, mutation, role }) {
  if (!applications.length) {
    return <EmptyState title="Откликов пока нет" />;
  }

  return (
    <div className={styles.applications}>
      {applications.map((application) => (
        <article className={styles.applicationRow} key={application.id}>
          <div>
            <span>{application.hunterCallsign}</span>
            <h3>{application.orderTitle}</h3>
            <p>{application.message || "Сообщение не приложено."}</p>
            <small>
              {application.proposedReward
                ? formatReward(application.proposedReward, "CR", "CREDITS")
                : "Без изменения награды"}
            </small>
          </div>
          <StatusBadge
            tone={
              APPLICATION_STATUS_OPTIONS.find((option) => option.value === application.status)
                ? "info"
                : "neutral"
            }
          >
            {applicationStatusLabel(application.status)}
          </StatusBadge>
          <div className={styles.rowActions}>
            {role === "client" && application.status === "PENDING" ? (
              <>
                <button
                  className="button"
                  type="button"
                  onClick={() =>
                    mutation.mutate({ action: "accept", applicationId: application.id })
                  }
                >
                  <Check size={16} aria-hidden="true" />
                  Принять
                </button>
                <button
                  className="button danger"
                  type="button"
                  onClick={() =>
                    mutation.mutate({ action: "reject", applicationId: application.id })
                  }
                >
                  <X size={16} aria-hidden="true" />
                  Отклонить
                </button>
              </>
            ) : null}
            {role === "hunter" && application.status === "PENDING" ? (
              <button
                className="button danger"
                type="button"
                onClick={() =>
                  mutation.mutate({ action: "withdraw", applicationId: application.id })
                }
              >
                <Trash2 size={16} aria-hidden="true" />
                Отозвать
              </button>
            ) : null}
          </div>
        </article>
      ))}
      {mutation.isError ? (
        <div className="notice error">{getApiErrorMessage(mutation.error)}</div>
      ) : null}
    </div>
  );
}

function FormShell({ children, error, isPending, onSubmit, submitIcon: Icon, submitLabel }) {
  return (
    <form
      className={styles.form}
      onSubmit={(event) => {
        event.preventDefault();
        onSubmit();
      }}
    >
      {children}
      {error ? <div className="notice error">{getApiErrorMessage(error)}</div> : null}
      <button className="button" type="submit" disabled={isPending}>
        <Icon size={17} aria-hidden="true" />
        {submitLabel}
      </button>
    </form>
  );
}

function InputField({ form, label, name, setForm, type = "text", ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input
        className="input"
        name={name}
        type={type}
        value={form[name]}
        onChange={(event) =>
          setForm((current) => ({
            ...current,
            [name]: event.target.value,
          }))
        }
        {...props}
      />
    </label>
  );
}

function TextAreaField({ form, label, name, setForm, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <textarea
        className="textarea"
        name={name}
        value={form[name]}
        onChange={(event) =>
          setForm((current) => ({
            ...current,
            [name]: event.target.value,
          }))
        }
        {...props}
      />
    </label>
  );
}

function SelectField({
  form,
  label,
  name,
  options,
  setForm,
  valueKey = "id",
  labelKey = "name",
  required,
}) {
  return (
    <label className="field">
      <span>{label}</span>
      <select
        className="select"
        name={name}
        required={required}
        value={form[name] ?? ""}
        onChange={(event) =>
          setForm((current) => ({
            ...current,
            [name]: event.target.value,
          }))
        }
      >
        <option value="">Не указано</option>
        {options.map((option) => (
          <option key={option[valueKey]} value={option[valueKey]}>
            {option[labelKey] ?? option.label}
          </option>
        ))}
      </select>
    </label>
  );
}
