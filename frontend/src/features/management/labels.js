export function roleLabel(role) {
  return {
    ADMIN: "Админ",
    CLIENT: "Заказчик",
    HUNTER: "Охотник",
    MODERATOR: "Модератор",
  }[role] ?? role;
}

export function statusLabel(status) {
  return {
    ACTIVE: "Активен",
    BLOCKED: "Заблокирован",
    DELETED: "Удален",
  }[status] ?? status;
}

export function toneForUserStatus(status) {
  return {
    ACTIVE: "success",
    BLOCKED: "danger",
    DELETED: "muted",
  }[status] ?? "neutral";
}

export function dictionaryLabel(value) {
  return {
    ACTIVE: "Активна",
    ALLIED: "Союзники",
    CORPORATION: "Корпорация",
    FRIENDLY: "Дружественно",
    GOVERNMENT: "Правительство",
    GUILD: "Гильдия",
    HOSTILE: "Враждебно",
    NEUTRAL: "Нейтрально",
    PIRATES: "Пираты",
    REBELS: "Повстанцы",
    RESTRICTED: "Ограничена",
    UNAVAILABLE: "Недоступна",
  }[value] ?? value;
}
