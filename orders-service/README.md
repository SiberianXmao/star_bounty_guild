# Orders Service

Сервис контрактов гильдии. Отвечает за заказы, заявки охотников, отзывы, смену статусов и публикацию событий через transactional outbox.

Данные лежат в базе `bounty_orders`. Через Feign сервис обращается к `user-service`, `profiles-service` и `dictionary-service`. При принятии заявки публикуется Kafka-событие `bounty.applications.application-accepted.v1`.

## API

- `/api/v1/orders` - заказы и публичная доска контрактов;
- `/api/v1/applications` - заявки охотников;
- `/api/v1/orders/{orderId}/review` - создание отзыва по завершённому заказу;
- `/api/v1/reviews/hunters/{hunterProfileId}` - отзывы конкретного охотника;
- `/api/v1/reviews/my/client` - отзывы, оставленные текущим заказчиком.

## Карта пакетов

| Пакет | Назначение |
| --- | --- |
| `applications.api` | Внутренние контракты заявок |
| `applications.domain` | JPA-сущности заявок |
| `applications.domain.enums` | Статусы заявок |
| `applications.repository` | Репозитории заявок |
| `applications.service` | Контракты application-сценариев |
| `applications.service.impl` | Реализация обработки заявок |
| `applications.web` | REST API заявок |
| `applications.web.dto` | DTO заявок |
| `common.exception` | Исключения и global handler |
| `common.messaging` | Общие Kafka/message DTO |
| `common.outbox` | Transactional outbox и публикация событий |
| `common.persistence` | Базовые JPA-сущности |
| `config` | Security, Feign, Kafka и общая конфигурация |
| `integrations.dictionary` | Feign-клиент к `dictionary-service` |
| `integrations.profiles` | Feign-клиент к `profiles-service` |
| `integrations.users` | Feign-клиент к `user-service` |
| `internal.auth` | Проверка `X-Internal-Token` |
| `orders.api` | Внутренние контракты order-domain |
| `orders.domain` | JPA-сущность заказа |
| `orders.domain.enums` | Статусы и параметры заказа |
| `orders.mapper` | Мапперы request/entity/response |
| `orders.repository` | Репозитории и specifications заказов |
| `orders.service` | Контракты order-сценариев |
| `orders.service.impl` | Основная orchestration-логика заказов |
| `orders.service.support` | Вспомогательные компоненты: policies, resolvers, assemblers |
| `orders.web` | REST API заказов |
| `orders.web.dto` | DTO заказов |
| `reviews.api` | Внутренние контракты отзывов |
| `reviews.domain` | JPA-сущности отзывов |
| `reviews.repository` | Репозитории отзывов |
| `reviews.service` | Бизнес-логика отзывов |
| `reviews.web` | REST API отзывов |
| `reviews.web.dto` | DTO отзывов |

## Запуск

```powershell
docker compose up -d --build orders-postgres kafka user-service dictionary-service profiles-service orders-service
```

- сервис: http://localhost:8084
- Swagger: http://localhost:8084/swagger-ui.html
- миграции: `src/main/resources/db/migration`
