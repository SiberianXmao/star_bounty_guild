# Profiles Service

Сервис профилей участников гильдии. Хранит профили заказчиков и охотников, навыки, доступность, рейтинг и статистику выполненных заказов.

Данные лежат в базе `bounty_profiles`. Пользователи приходят из `user-service`, справочные данные - из `dictionary-service`, события рейтинга - из Kafka.

## API

- публичный префикс: `/api/v1/profiles`;
- внутренний префикс: `/internal/v1/profiles`.

## Карта пакетов

| Пакет | Назначение |
| --- | --- |
| `common.exception` | Исключения и глобальный exception handler |
| `common.persistence` | Базовые JPA-сущности |
| `config` | Security, Feign, Kafka и общая конфигурация |
| `dictionary` | Feign-интеграция с `dictionary-service` |
| `domain` | JPA-сущности профилей |
| `domain.enums` | Enum-типы профилей |
| `internal` | Internal REST API для других сервисов |
| `internal.auth` | Проверка `X-Internal-Token` |
| `internal.dto` | DTO internal lookup |
| `rating.domain` | Сущности рейтинга и отзывов |
| `rating.messaging` | Kafka listeners для событий рейтинга |
| `rating.repository` | Репозитории рейтинга |
| `rating.service` | Пересчёт рейтинга и статистики |
| `repository` | Репозитории профилей |
| `service` | Контракты profile-сценариев |
| `service.impl` | Реализация бизнес-логики профилей |
| `users` | Feign-интеграция с `user-service` |
| `web` | REST-контроллеры публичного API |
| `web.dto` | DTO profile API |

## Запуск

```powershell
docker compose up -d --build profiles-postgres user-service dictionary-service kafka profiles-service
```

- сервис: http://localhost:8083
- Swagger: http://localhost:8083/swagger-ui.html
- миграции: `src/main/resources/db/migration`
