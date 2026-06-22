# Microservice Readiness Status

## Где Проект Сейчас

Проект уже находится в учебной микросервисной архитектуре, а не в простом монолите.

Вынесены отдельные сервисы:

- `dictionary-service` со своей базой `bounty_dictionary`;
- `profiles-service` со своей базой `bounty_profiles`;
- `orders-service` со своей базой `bounty_orders`;
- `notification-service` со своей базой `bounty_notifications`;
- `keycloak` как Identity Provider;
- `kafka` как брокер доменных событий.

`user-service` теперь отвечает за:

- `users/auth`;
- публичные и админские user API;
- internal user API для других сервисов.

## Что Уже Микросервисно

`dictionary-service`:

- отдельный Spring Boot app;
- отдельная PostgreSQL база;
- отдельные Flyway migrations;
- другие сервисы ходят в него через HTTP/Feign;
- user-service больше не хранит dictionary JPA/domain/repository.

`profiles-service`:

- отдельный Spring Boot app;
- отдельная PostgreSQL база;
- отдельные Flyway migrations;
- публичные `/api/v1/profiles/**` идут через gateway в profiles-service;
- orders-service ходит в profiles-service через Feign;
- user-service больше не хранит profile JPA/domain/repository.

`orders-service`:

- отдельный Spring Boot app;
- отдельная PostgreSQL база `bounty_orders`;
- владеет `orders`, `order_applications`, `order_offers`;
- владеет локальным `outbox_events`;
- публичные `/api/v1/orders/**` и `/api/v1/applications/**` идут через gateway в orders-service;
- синхронно ходит в dictionary-service, profiles-service и user-service через Feign;
- асинхронно публикует `applications.application-accepted.v1` в Kafka.

`notification-service`:

- отдельный Spring Boot app;
- отдельная PostgreSQL база;
- получает события через Kafka;
- не вызывается синхронно из бизнес-операций orders-service.

Keycloak:

- frontend использует Authorization Code + PKCE;
- user-service, profiles-service и orders-service работают как OAuth2 Resource Server;
- роли приходят из Keycloak JWT.

Internal service auth:

- `/internal/**` в user-service, dictionary-service, profiles-service и orders-service требует header `X-Internal-Token`;
- Feign-клиенты автоматически добавляют этот header через `RequestInterceptor`;
- общий dev-token прокидывается через `APP_INTERNAL_AUTH_TOKEN`.

## Что Еще Не Чисто

`users/auth` уже оформлен как отдельный compose-сервис `user-service`.
Исходники пока физически лежат в папке `backend/`, а Java package все еще называется `com.stud.backend`.
Это оставлено как безопасный промежуточный этап, чтобы не смешивать инфраструктурное переименование с большим package rename.

Сейчас profiles-service и orders-service вызывают user-service internal users API:

```text
GET  /internal/v1/users/by-email?email={email}
POST /internal/v1/users/{userId}/roles
```

Это уже нормальная учебная синхронная интеграция через HTTP/Feign.

Также в user-service БД еще есть legacy-таблицы после detach migrations:

```text
legacy_orders
legacy_order_applications
legacy_order_offers
legacy_outbox_events
legacy_client_profiles
legacy_hunter_profiles
legacy_dictionary tables
```

Они оставлены как след старой схемы и для безопасной миграции. Новые сервисы их не читают.

## Где Мы По Этапам

Текущий этап: основная доменная нарезка завершена для учебного проекта.

Уже есть:

```text
gateway
frontend
user-service
dictionary-service
profiles-service
orders-service
notification-service
keycloak
kafka
postgres per service
internal token guard
```

До "чистого учебного микросервиса" остался один косметически-архитектурный шаг:

1. Физически переименовать папку `backend/` и Java package `com.stud.backend` под user-service naming.

До production-уровня нужно больше:

- заменить общий static token на OAuth2 client credentials, mTLS или другой полноценный service identity;
- service discovery или нормальная config/env стратегия;
- distributed tracing;
- централизованные логи;
- contract tests для Feign и Kafka;
- healthchecks/readiness checks;
- миграционная стратегия для реальных данных.

## Следующий Технический Разрез

Следующий разумный шаг: довести naming user-service до конца.

Варианты:

1. Переименовать папку `backend/` в `user-service/`.
2. Потом, отдельным аккуратным шагом, переименовать Java package `com.stud.backend` в `com.stud.users`.
3. Позже перейти глубже в Keycloak: использовать Keycloak как основной источник пользователей и ролей, а локальный user-service держать только application metadata.

Для учебного проекта самый понятный путь теперь:

```text
backend folder/package naming -> user-service naming
```

Бизнес-ответственность уже сужена до пользователей, ролей, auth и админских операций.
Остался naming debt.

## Критерии Чистоты

Минимальный учебный критерий:

1. Каждый сервис имеет свою базу данных.
2. Сервисы не читают таблицы друг друга напрямую.
3. Между сервисами есть только HTTP contracts, Feign clients или Kafka events.
4. Внутри сервиса нет импортов чужих `domain`, `repository`, `service`, `web`.
5. Таблицы одного сервиса не имеют foreign key на таблицы другого сервиса.
6. Авторизация идет через Keycloak JWT.
7. Internal endpoints не открыты без service-to-service trust.

Пункты 1-7 уже в основном выполнены для текущей учебной архитектуры.
Главный оставшийся долг: завершить физическое переименование user-service source/package.
