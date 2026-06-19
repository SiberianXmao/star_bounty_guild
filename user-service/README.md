# User Service

Сервис пользователей и управления доступом.

## Отвечает за

- регистрацию, вход, refresh token и выход;
- пользователей и их статусы;
- роли `ADMIN`, `MODERATOR`, `CLIENT`, `HUNTER`;
- административные и модераторские операции;
- внутренний поиск пользователей для других сервисов.

Сервис хранит данные в базе `bounty_users`. Для получения профилей и справочных данных использует Feign clients.

## API

- `/api/v1/auth` - авторизация;
- `/api/v1/users` - управление пользователями;
- `/api/v1/moderation` - модерация;
- `/internal/v1/users` - internal API.

## Запуск

```powershell
docker compose up -d --build user-postgres user-service
```

- внутренний порт: `8080`
- Swagger через gateway: http://localhost:3000/swagger-ui/index.html
- миграции: `src/main/resources/db/migration`
