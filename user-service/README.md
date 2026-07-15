# User Service

Сервис пользователей и управления доступом. Отвечает за регистрацию, вход, роли, статусы пользователей, модерацию и пользовательские аватары.

Данные лежат в базе `bounty_users`. Авторизация интегрирована с Keycloak, а изображения аватаров загружаются через `file-service`.

## API

- `/api/v1/auth` - регистрация, вход, refresh token и logout;
- `/api/v1/users` - управление пользователями;
- `/api/v1/users/me/avatar` - загрузка и удаление аватара;
- `/api/v1/moderation` - модераторские операции;
- `/internal/v1/users` - internal API для других сервисов.

## Карта пакетов

| Пакет | Назначение |
| --- | --- |
| `auth.config` | Настройки безопасности и выбора auth provider |
| `auth.domain` | Сущности refresh/session/token-логики |
| `auth.keycloak` | Интеграция с Keycloak Admin API |
| `auth.repository` | Репозитории auth-данных |
| `auth.service` | Контракты auth-сценариев |
| `auth.service.impl` | Реализация регистрации, входа и refresh |
| `auth.web` | Auth REST-контроллеры |
| `auth.web.dto` | DTO auth API |
| `common.config` | Общие настройки приложения |
| `common.exception` | Исключения и глобальный exception handler |
| `common.persistence` | Базовые JPA-сущности |
| `internal` | Internal endpoints для межсервисного доступа |
| `media.client` | Feign-клиент к `file-service` |
| `media.exception` | Ошибки работы с файлами |
| `media.service` | Сценарии загрузки/удаления аватара |
| `media.web` | REST API пользовательских аватаров |
| `media.web.dto` | DTO media API |
| `users.api` | Внутренние контракты user-domain |
| `users.domain` | JPA-сущности пользователей и ролей |
| `users.domain.enums` | Статусы и роли |
| `users.internal` | Internal user lookup |
| `users.repository` | Spring Data JPA repositories |
| `users.service` | Контракты user-сценариев |
| `users.service.impl` | Реализация управления пользователями |
| `users.web` | REST-контроллеры пользователей/админки |
| `users.web.dto` | DTO user API |

## Запуск

```powershell
docker compose up -d --build file-service user-postgres keycloak user-service
```

- внутренний порт: `8080`
- Swagger через gateway: http://localhost:3000/swagger-ui/index.html
- миграции: `src/main/resources/db/migration`
