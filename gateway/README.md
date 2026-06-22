# Gateway

Edge reverse proxy на Nginx. Это единая точка входа в приложение; бизнес-логики здесь нет.

## Маршруты

| Путь | Сервис |
| --- | --- |
| `/api/v1/dictionary/**` | dictionary-service |
| `/api/v1/profiles/**` | profiles-service |
| `/api/v1/orders/**` | orders-service |
| `/api/v1/applications/**` | orders-service |
| `/api/v1/notifications/**` | notification-service |
| `/api/**` | user-service |
| `/` | frontend |

## Запуск

```powershell
docker compose up -d --build gateway
```

Адрес: http://localhost:3000
