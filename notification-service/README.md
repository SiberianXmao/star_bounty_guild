# Notification Service

Сервис пользовательских уведомлений. Получает доменные события из Kafka, сохраняет уведомления и отдаёт их через REST API.

Сейчас сервис обрабатывает событие:

```text
bounty.applications.application-accepted.v1
```

Данные лежат в базе `bounty_notifications`.

## API

- `/api/v1/notifications` - список уведомлений и операции пользователя.

## Карта пакетов

| Пакет | Назначение |
| --- | --- |
| `domain` | JPA-сущность уведомления |
| `messaging` | Kafka consumers и обработчики событий |
| `repository` | Spring Data JPA repositories |
| `service` | Контракты notification-сценариев |
| `service.impl` | Реализация бизнес-логики уведомлений |
| `web` | REST API уведомлений |
| `web.dto` | DTO notification API |

## Запуск

```powershell
docker compose up -d --build notification-postgres kafka notification-service
```

- сервис: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui.html
