# Bounty Guild

Учебный fullstack-проект в стиле гильдии охотников за наградой.

## Запуск

```bash
docker compose up -d --build
```

Основные адреса:

- приложение через edge gateway: http://localhost:3000
- user-service Swagger через gateway: http://localhost:3000/swagger-ui/index.html
- dictionary-service Swagger: http://localhost:8081/swagger-ui.html
- notification-service Swagger: http://localhost:8082/swagger-ui.html
- profiles-service Swagger: http://localhost:8083/swagger-ui.html
- orders-service Swagger: http://localhost:8084/swagger-ui.html
- Keycloak admin console: http://localhost:8085

## Сервисы

```text
gateway               edge reverse proxy
frontend              React frontend
user-service          users/auth, roles, user/admin API
dictionary-service    микросервис справочников
profiles-service      микросервис профилей
orders-service        микросервис заказов, заявок и order outbox
notification-service  микросервис уведомлений
kafka                 брокер доменных событий
keycloak              identity provider
user-postgres         база user-service
dictionary-postgres   база dictionary-service
profiles-postgres     база profiles-service
orders-postgres       база orders-service
notification-postgres база notification-service
keycloak-postgres     база Keycloak
```

## Gateway Routes

```text
/api/v1/dictionary/**      -> dictionary-service
/api/v1/profiles/**        -> profiles-service
/api/v1/orders/**          -> orders-service
/api/v1/applications/**    -> orders-service
/api/v1/notifications/**   -> notification-service
/api/**                    -> user-service
/swagger-ui/**             -> user-service
/v3/api-docs/**            -> user-service
/                         -> frontend
```

## Service Databases

```text
user-service          bounty_users
dictionary-service    bounty_dictionary
profiles-service      bounty_profiles
orders-service        bounty_orders
notification-service  bounty_notifications
keycloak              keycloak
```

User-service source currently lives in `backend/`. Detach migrations:

```text
backend/src/main/resources/db/migration/V4__detach_dictionary_service.sql
backend/src/main/resources/db/migration/V5__detach_profile_service.sql
backend/src/main/resources/db/migration/V6__detach_order_service.sql
backend/src/main/resources/db/migration/V101__detach_notification_service.sql
```

## Интеграции

Services -> dictionary-service:

```text
DictionaryServiceFeignClient
```

Orders-service -> profiles-service:

```text
ProfileServiceFeignClient
```

Profiles-service/orders-service -> user-service:

```text
GET  /internal/v1/users/by-email?email={email}
POST /internal/v1/users/{userId}/roles
```

Orders-service -> Kafka -> notification-service:

```text
applications.application-accepted.v1
bounty.applications.application-accepted.v1
```

## Internal Auth

Internal endpoints are protected by a shared service token:

```text
Header: X-Internal-Token
Env:    APP_INTERNAL_AUTH_TOKEN
```

Docker Compose uses `dev-internal-token` by default. For a different local token:

```bash
APP_INTERNAL_AUTH_TOKEN=your-dev-token docker compose up -d --build
```

Feign clients add the header automatically through `RequestInterceptor`.

## Keycloak

Docker Compose запускает frontend и resource servers в Keycloak-режиме.

```text
admin console:   http://localhost:8085
admin user:      admin
admin password:  admin
realm:           bounty-guild
frontend client: bounty-frontend
demo password:   password
```

Демо-пользователи:

```text
admin@bounty.local
client.hutt@bounty.local
client.rebel@bounty.local
hunter.raven@bounty.local
hunter.rho@bounty.local
hunter.specter@bounty.local
```

Frontend использует Authorization Code + PKCE.
User-service, profiles-service и orders-service работают как OAuth2 Resource Server.

Если realm уже был импортирован в существующую Keycloak-базу, изменения в `keycloak/realm/bounty-guild-realm.json` не применятся автоматически. Для чистого dev-импорта нужно пересоздать volume `bounty_keycloak_postgres_data`.

## Статус Микросервисов

Подробная карта текущего состояния:

```text
docs/microservice-readiness-status.md
```

Сейчас уже есть:

```text
HTTP sync:    services -> dictionary-service через Feign
HTTP sync:    orders-service -> profiles-service через Feign
HTTP sync:    services -> user-service через Feign
Async event:  orders-service -> Kafka -> notification-service
OIDC auth:    frontend -> Keycloak -> resource servers
Internal API: X-Internal-Token guard
```

Следующий крупный кандидат на чистку: физически переименовать папку `backend/` и Java package `com.stud.backend` в `user-service` naming.
