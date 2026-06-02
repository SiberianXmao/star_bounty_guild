export const RISK_OPTIONS = [
  { value: "LOW", label: "Низкий" },
  { value: "MEDIUM", label: "Средний" },
  { value: "HIGH", label: "Высокий" },
  { value: "EXTREME", label: "Крайний" },
];

export const URGENCY_OPTIONS = [
  { value: "LOW", label: "Неспешно" },
  { value: "NORMAL", label: "Обычная" },
  { value: "HIGH", label: "Срочно" },
  { value: "CRITICAL", label: "Критично" },
];

export const STATUS_OPTIONS = [
  { value: "DRAFT", label: "Черновик" },
  { value: "OPEN", label: "Открыт" },
  { value: "OFFERED", label: "Предложен" },
  { value: "ASSIGNED", label: "Назначен" },
  { value: "IN_PROGRESS", label: "В работе" },
  { value: "SUBMITTED", label: "На проверке" },
  { value: "COMPLETED", label: "Завершен" },
  { value: "CANCELLED", label: "Отменен" },
  { value: "DISPUTED", label: "Спор" },
  { value: "ARCHIVED", label: "Архив" },
];

export const ACCEPTANCE_OPTIONS = [
  { value: "APPLICATIONS", label: "Отклики" },
  { value: "INSTANT_ACCEPT", label: "Быстрое принятие" },
  { value: "PERSONAL_OFFER", label: "Персонально" },
];

export const VISIBILITY_OPTIONS = [
  { value: "PUBLIC", label: "Публичный" },
  { value: "PRIVATE", label: "Приватный" },
];

export const AVAILABILITY_OPTIONS = [
  { value: "AVAILABLE", label: "Доступен" },
  { value: "BUSY", label: "Занят" },
  { value: "UNAVAILABLE", label: "Недоступен" },
];

export const APPLICATION_STATUS_OPTIONS = [
  { value: "PENDING", label: "Ожидает" },
  { value: "ACCEPTED", label: "Принят" },
  { value: "REJECTED", label: "Отклонен" },
  { value: "WITHDRAWN", label: "Отозван" },
];

export function optionLabel(options, value) {
  return options.find((option) => option.value === value)?.label ?? value ?? "Не указано";
}

export function statusLabel(value) {
  return optionLabel(STATUS_OPTIONS, value);
}

export function riskLabel(value) {
  return optionLabel(RISK_OPTIONS, value);
}

export function urgencyLabel(value) {
  return optionLabel(URGENCY_OPTIONS, value);
}

export function acceptanceLabel(value) {
  return optionLabel(ACCEPTANCE_OPTIONS, value);
}

export function availabilityLabel(value) {
  return optionLabel(AVAILABILITY_OPTIONS, value);
}

export function applicationStatusLabel(value) {
  return optionLabel(APPLICATION_STATUS_OPTIONS, value);
}

export function toneForRisk(value) {
  return {
    LOW: "calm",
    MEDIUM: "neutral",
    HIGH: "warn",
    EXTREME: "danger",
  }[value] ?? "neutral";
}

export function toneForStatus(value) {
  return {
    OPEN: "calm",
    DRAFT: "muted",
    ASSIGNED: "info",
    IN_PROGRESS: "warn",
    SUBMITTED: "info",
    COMPLETED: "success",
    CANCELLED: "danger",
    DISPUTED: "danger",
    OFFERED: "warn",
    ARCHIVED: "muted",
  }[value] ?? "neutral";
}
