# Bounty Guild

Учебный fullstack-проект по мотивам Star Wars: гильдия охотников за наградой. Заказчики публикуют контракты, охотники ведут профили и подают заявки, администраторы управляют пользователями и справочниками.

Проект развивается как учебная микросервисная система: каждый backend-сервис владеет своей базой данных, синхронные запросы идут через REST/Feign, доменные события передаются через Kafka.

## Возможности

- регистрация и авторизация пользователей через Keycloak;
- роли `ADMIN`, `MODERATOR`, `CLIENT`, `HUNTER`;
- профили заказчиков и охотников;
- публичная доска контрактов с фильтрами;
- заявки охотников и смена статусов заказов;
- рейтинг охотников и отзывы;
- справочники планет, секторов, фракций, валют, категорий и навыков;
- загрузка аватаров и изображений справочника через MinIO;
- административная и модераторская панели;
- уведомления по событиям заказов;
- метрики, dashboards и централизованные логи.

## Архитектура

| Компонент | Назначение |
| --- | --- |
| `gateway` | Единая точка входа, маршрутизация frontend/API/media |
| `frontend` | React/Vite интерфейс приложения |
| `user-service` | Пользователи, роли, auth-интеграция, модерация, аватары |
| `dictionary-service` | Планеты, сектора, фракции, валюты, категории, навыки |
| `file-service` | Проверка изображений и работа с MinIO |
| `profiles-service` | Профили заказчиков/охотников, навыки, рейтинг |
| `orders-service` | Заказы, заявки, отзывы, outbox и Kafka-события |
| `notification-service` | Получение событий и хранение уведомлений |
| `keycloak` | Identity provider, пользователи и realm roles |
| `kafka` | Доменные события между сервисами |
| `redis` | Кэш справочников |
| `elasticsearch` | Хранение и поиск логов |
| `logstash` | Приём JSON-логов от сервисов и запись в Elasticsearch |
| `kibana` | Просмотр логов из Elasticsearch |
| `prometheus` | Сбор метрик Spring Actuator/Micrometer |
| `grafana` | Дашборды и визуализация метрик |

## Технологии

- Java 21, Spring Boot, Spring Security, Spring Data JPA;
- Spring Cloud OpenFeign;
- PostgreSQL и Flyway;
- Redis и Spring Cache;
- MinIO как S3-compatible object storage;
- Apache Kafka;
- Keycloak;
- React, Vite, TanStack Query;
- Nginx и Docker Compose;
- Logback, Logstash, Elasticsearch, Kibana;
- Micrometer, Prometheus, Grafana.

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

Команда `docker compose down -v` дополнительно удалит данные PostgreSQL, Redis, MinIO, Prometheus, Grafana и Elasticsearch.

## Адреса

- приложение: http://localhost:3000
- user-service Swagger: http://localhost:3000/swagger-ui/index.html
- dictionary-service Swagger: http://localhost:8081/swagger-ui.html
- notification-service Swagger: http://localhost:8082/swagger-ui.html
- profiles-service Swagger: http://localhost:8083/swagger-ui.html
- orders-service Swagger: http://localhost:8084/swagger-ui.html
- Keycloak: http://localhost:8085
- MinIO Console: http://localhost:9001
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

Grafana использует логин `admin` и пароль `GRAFANA_ADMIN_PASSWORD` из `.env`.

## Логи

`dictionary-service` уже настроен на структурированные логи:

```text
dictionary-service -> Logback -> Logstash -> Elasticsearch -> Kibana
```

Индексы создаются в формате:

```text
bounty-logs-dictionary-service-YYYY.MM.dd
```

В Kibana нужно создать Data View `bounty-logs-*` с timestamp field `@timestamp`, затем открыть `Analytics -> Discover`.

Уровень логирования dictionary-service меняется через `.env`:

```env
DICTIONARY_LOG_LEVEL=INFO
```

Для подробной диагностики можно временно поставить `DEBUG`.

## Метрики

Сервисы отдают метрики через Spring Actuator/Micrometer на management-портах. Prometheus собирает метрики, Grafana показывает dashboards. Подробности находятся в [`monitoring/README.md`](monitoring/README.md).

## Взаимодействие

```text
Frontend -> Gateway -> REST services
Orders/Profile/User services -> Feign -> internal REST API
Orders service -> Kafka -> Notification/Profile services
Services -> Keycloak/JWT validation
Services -> Logback/Logstash -> Elasticsearch/Kibana
Services -> Actuator/Micrometer -> Prometheus/Grafana
```

Внутренние endpoint защищены заголовком `X-Internal-Token`. Значение задаётся переменной `APP_INTERNAL_AUTH_TOKEN` в `.env`.
