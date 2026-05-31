import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link, useParams } from "react-router-dom";
import {
  ArrowLeft,
  CalendarClock,
  CheckCircle2,
  Coins,
  FileText,
  MapPin,
  Send,
  ShieldAlert,
  Star,
  UserRound,
} from "lucide-react";
import Modal from "../components/Modal.jsx";
import StatusBadge from "../components/StatusBadge.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import { applicationsApi, ordersApi, profilesApi } from "../services/bountyApi.js";
import { getApiErrorMessage } from "../services/apiClient.js";
import { formatDate, formatReward, normalizeOptionalString, shortId } from "../utils/formatters.js";
import {
  acceptanceLabel,
  riskLabel,
  statusLabel,
  toneForRisk,
  toneForStatus,
  urgencyLabel,
} from "../utils/labels.js";
import styles from "./OrderDetailsPage.module.css";

const lifecycle = ["OPEN", "ASSIGNED", "IN_PROGRESS", "SUBMITTED", "COMPLETED"];

export default function OrderDetailsPage() {
  const { orderId } = useParams();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useAuth();
  const [isApplyOpen, setIsApplyOpen] = useState(false);
  const [applicationForm, setApplicationForm] = useState({
    message: "",
    proposedReward: "",
  });

  const orderQuery = useQuery({
    queryKey: ["order", orderId],
    queryFn: () => ordersApi.publicOrder(orderId),
  });

  const profilesQuery = useQuery({
    queryKey: ["profiles", "me"],
    queryFn: profilesApi.me,
    enabled: isAuthenticated,
  });

  const applyMutation = useMutation({
    mutationFn: () =>
      applicationsApi.apply(orderId, {
        message: normalizeOptionalString(applicationForm.message),
        proposedReward: applicationForm.proposedReward || null,
      }),
    onSuccess: () => {
      setIsApplyOpen(false);
      setApplicationForm({ message: "", proposedReward: "" });
      queryClient.invalidateQueries({ queryKey: ["applications"] });
    },
  });

  if (orderQuery.isLoading) {
    return <div className={styles.loading}>Загрузка контракта...</div>;
  }

  if (orderQuery.isError) {
    return (
      <div className={styles.page}>
        <Link className={styles.backLink} to="/">
          <ArrowLeft size={17} aria-hidden="true" />
          Назад
        </Link>
        <div className="notice error">{getApiErrorMessage(orderQuery.error)}</div>
      </div>
    );
  }

  const order = orderQuery.data;
  const activeStep = Math.max(lifecycle.indexOf(order.status), 0);
  const hunterProfile = profilesQuery.data?.hunterProfile;
  const canApply =
    isAuthenticated &&
    hunterProfile &&
    order.status === "OPEN" &&
    order.acceptanceMode === "APPLICATIONS";

  return (
    <div className={styles.page}>
      <Link className={styles.backLink} to="/">
        <ArrowLeft size={17} aria-hidden="true" />
        Доска контрактов
      </Link>

      <section className={styles.contract}>
        <article className={styles.briefing}>
          <div className={styles.idLine}>
            <span>#{shortId(order.id)}</span>
            <div className={styles.badges}>
              <StatusBadge tone={toneForStatus(order.status)}>{statusLabel(order.status)}</StatusBadge>
              <StatusBadge tone={toneForRisk(order.riskLevel)}>{riskLabel(order.riskLevel)}</StatusBadge>
            </div>
          </div>

          <h1>{order.title}</h1>
          <p className={styles.description}>{order.description}</p>

          <div className={styles.requirements}>
            <FileText size={18} aria-hidden="true" />
            <div>
              <h2>Требования</h2>
              <p>{order.requirements || "Дополнительные требования не указаны."}</p>
            </div>
          </div>

          <div className={styles.timeline} aria-label="Жизненный цикл заказа">
            {lifecycle.map((step, index) => (
              <span className={index <= activeStep ? styles.done : ""} key={step}>
                <CheckCircle2 size={16} aria-hidden="true" />
                {statusLabel(step)}
              </span>
            ))}
          </div>
        </article>

        <aside className={styles.sidePanel}>
          <div className={styles.reward}>
            <span>Награда</span>
            <strong>
              {formatReward(order.rewardAmount, order.rewardCurrencySymbol, order.rewardCurrencyCode)}
            </strong>
          </div>

          <div className={styles.dataRows}>
            <span>
              <MapPin size={17} aria-hidden="true" />
              {order.planetName || order.sectorName || "Локация скрыта"}
            </span>
            <span>
              <CalendarClock size={17} aria-hidden="true" />
              {formatDate(order.deadline)}
            </span>
            <span>
              <ShieldAlert size={17} aria-hidden="true" />
              {urgencyLabel(order.urgencyLevel)} · {acceptanceLabel(order.acceptanceMode)}
            </span>
            <span>
              <Coins size={17} aria-hidden="true" />
              {order.categoryName}
            </span>
          </div>

          <div className={styles.client}>
            <UserRound size={19} aria-hidden="true" />
            <div>
              <span>Заказчик</span>
              <strong>{order.clientName}</strong>
              <small>
                <Star size={14} aria-hidden="true" />
                {order.clientAverageRating ?? "0.0"} · {order.clientReliabilityScore ?? 0}
              </small>
            </div>
          </div>

          {canApply ? (
            <button className="button" type="button" onClick={() => setIsApplyOpen(true)}>
              <Send size={17} aria-hidden="true" />
              Откликнуться
            </button>
          ) : isAuthenticated ? (
            <div className="notice">
              {hunterProfile
                ? "Отклик недоступен для текущего состояния."
                : "Нужен профиль охотника в кабинете."}
            </div>
          ) : (
            <div className="notice">Войдите, чтобы отправить отклик.</div>
          )}
        </aside>
      </section>

      <Modal isOpen={isApplyOpen} onClose={() => setIsApplyOpen(false)} title="Отклик на контракт">
        <form
          className={styles.applyForm}
          onSubmit={(event) => {
            event.preventDefault();
            applyMutation.mutate();
          }}
        >
          <label className="field">
            <span>Сообщение</span>
            <textarea
              className="textarea"
              value={applicationForm.message}
              onChange={(event) =>
                setApplicationForm((current) => ({
                  ...current,
                  message: event.target.value,
                }))
              }
            />
          </label>
          <label className="field">
            <span>Предложенная награда</span>
            <input
              className="input"
              min="0"
              type="number"
              value={applicationForm.proposedReward}
              onChange={(event) =>
                setApplicationForm((current) => ({
                  ...current,
                  proposedReward: event.target.value,
                }))
              }
            />
          </label>
          {applyMutation.isError ? (
            <div className="notice error">{getApiErrorMessage(applyMutation.error)}</div>
          ) : null}
          <button className="button" type="submit" disabled={applyMutation.isPending}>
            <Send size={17} aria-hidden="true" />
            Отправить
          </button>
        </form>
      </Modal>
    </div>
  );
}
