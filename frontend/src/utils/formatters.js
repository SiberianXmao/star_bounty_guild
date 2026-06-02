export function compactParams(params) {
  return Object.fromEntries(
    Object.entries(params).filter(([, value]) => {
      if (value === null || value === undefined) {
        return false;
      }

      if (typeof value === "string") {
        return value.trim() !== "";
      }

      return true;
    }),
  );
}

export function formatReward(amount, symbol, code) {
  if (amount === null || amount === undefined) {
    return "Награда не указана";
  }

  const formattedAmount = new Intl.NumberFormat("ru-RU", {
    maximumFractionDigits: 2,
  }).format(Number(amount));

  return `${formattedAmount} ${symbol || code || ""}`.trim();
}

export function formatDate(value) {
  if (!value) {
    return "Не указано";
  }

  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

export function dateTimeLocalToIso(value) {
  return value ? new Date(value).toISOString() : null;
}

export function normalizeOptionalNumber(value) {
  return value === "" || value === null || value === undefined ? null : Number(value);
}

export function normalizeOptionalString(value) {
  return value?.trim() ? value.trim() : null;
}

export function shortId(value) {
  return value ? value.slice(0, 8).toUpperCase() : "--------";
}
