# Bounty Guild

Учебный fullstack-проект по мотивам Star Wars. Приложение представляет гильдию охотников за наградой: заказчики публикуют контракты, охотники ведут профили и подают заявки, а администраторы управляют пользователями и справочниками.

Проект построен как набор отдельных Spring Boot сервисов с собственными PostgreSQL базами. Синхронное взаимодействие выполняется через HTTP/Feign, события передаются через Kafka.

## Возможности

- регистрация и авторизация пользователей;
- роли `ADMIN`, `MODERATOR`, `CLIENT`, `HUNTER`;
- профили заказчиков и охотников;
- доска контрактов с фильтрами;
- заявки охотников и управление статусами заказов;
- справочники планет, секторов, фракций, валют и навыков;
- административная и модераторская панели;
- уведомления о событиях заказов.

## Архитектура

| Компонент | Назначение |
| --- | --- |
| `gateway` | Единая точка входа и маршрутизация запросов |
| `frontend` | React-интерфейс приложения |
| `user-service` | Пользователи, авторизация, роли и модерация |
| `dictionary-service` | Планеты, секторы, фракции, валюты, категории и навыки |
| `profiles-service` | Профили заказчиков и охотников |
| `orders-service` | Заказы, заявки, предложения и outbox |
| `notification-service` | Уведомления и Kafka consumer |
| `keycloak` | Identity provider и realm roles |
| `kafka` | Передача доменных событий |

Каждый backend-сервис владеет своей базой данных. Общих таблиц между сервисами нет.

## Технологии

- Java 21, Spring Boot, Spring Security, Spring Data JPA;
- Spring Cloud OpenFeign;
- PostgreSQL и Flyway;
- Apache Kafka;
- Keycloak;
- React, Vite, TanStack Query;
- Nginx и Docker Compose.

## Запуск

Создай локальный файл с секретами:

```powershell
Copy-Item .env.example .env
```

Замени значения `change-me` в `.env`, затем запусти проект:

```powershell
docker compose up -d --build
```

Проверить контейнеры:

```powershell
docker compose ps
```

Остановить проект:

```powershell
docker compose down
```

Удаление с флагом `-v` также удалит данные PostgreSQL.

## Адреса

- приложение: http://localhost:3000
- user-service Swagger: http://localhost:3000/swagger-ui/index.html
- dictionary-service Swagger: http://localhost:8081/swagger-ui.html
- notification-service Swagger: http://localhost:8082/swagger-ui.html
- profiles-service Swagger: http://localhost:8083/swagger-ui.html
- orders-service Swagger: http://localhost:8084/swagger-ui.html
- Keycloak: http://localhost:8085

## Взаимодействие

```text
Frontend -> Gateway -> REST services
Orders/Profile/User services -> Feign -> internal REST API
Orders service -> Kafka -> Notification service
Services -> Keycloak/JWT validation
```

Внутренние endpoint защищены заголовком `X-Internal-Token`. Значение задаётся переменной `APP_INTERNAL_AUTH_TOKEN` в `.env`.

Дополнительные архитектурные заметки находятся в папке [`docs`](docs).
