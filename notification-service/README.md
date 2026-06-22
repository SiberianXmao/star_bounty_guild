# Notification Service

Сервис пользовательских уведомлений.

## Отвечает за

- получение доменных событий из Kafka;
- сохранение уведомлений;
- выдачу уведомлений через REST API.

Сейчас сервис обрабатывает событие:

```text
bounty.applications.application-accepted.v1
```

Данные хранятся в базе `bounty_notifications`.

## Запуск

```powershell
docker compose up -d --build notification-postgres kafka notification-service
```

- сервис: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui.html
- API: `/api/v1/notifications`
