# Dictionary Service

Сервис справочников Bounty Guild. Хранит планеты, сектора, фракции, валюты, категории заказов и навыки охотников.

Данные лежат в базе `bounty_dictionary`. Публичные списки кэшируются в Redis, внутренние lookup-endpoint используются другими сервисами через Feign. Изображения справочника физически хранятся в `file-service`/MinIO, а здесь сохраняются только URL.

## API

- публичный префикс: `/api/v1/dictionary`;
- внутренний префикс: `/internal/v1/dictionary`;
- изображения: `/api/v1/dictionary/{planets|sectors|factions}/{id}/image`.

Internal API защищён заголовком `X-Internal-Token`.

## Карта пакетов

| Пакет | Назначение |
| --- | --- |
| `catalog.images` | Загрузка и удаление изображений планет, секторов и фракций |
| `common.exception` | Общие ошибки и глобальный обработчик исключений |
| `common.logging` | Request ID для логов и трассировки запроса |
| `common.persistence` | Базовые JPA-сущности |
| `config` | Security и общая конфигурация сервиса |
| `config.cache` | Redis cache names, TTL и обработка ошибок кэша |
| `db.migration` | Java-based Flyway migrations |
| `domain` | JPA-сущности справочников |
| `domain.enums` | Enum-типы предметной области |
| `integrations.files` | Feign-клиент к `file-service` |
| `internal` | Internal REST API для других сервисов |
| `internal.auth` | Проверка `X-Internal-Token` |
| `internal.dto` | DTO для межсервисных lookup-запросов |
| `mapper` | MapStruct-мапперы entity -> DTO/ref |
| `repository` | Spring Data JPA repositories |
| `service` | Публичный сервисный контракт |
| `service.impl` | Реализация бизнес-логики справочников |
| `web` | REST-контроллеры публичного API |
| `web.dto` | DTO публичного API |

## Логи

Сервис пишет структурированные логи через Logback в Logstash, дальше они попадают в Elasticsearch. В Kibana используй Data View `bounty-logs-*`.

Основные уровни:

- `INFO` - создание записей и работа с изображениями;
- `DEBUG` - чтение справочников, lookup и детали file-service;
- `WARN` - дубликаты, not found, validation и частично найденные lookup;
- `ERROR` - недоступность file-service и неожиданные исключения.

## Запуск

```powershell
docker compose up -d --build file-service dictionary-postgres redis elasticsearch logstash kibana dictionary-service
```

- сервис: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui.html
- Kibana: http://localhost:5601
- миграции: `src/main/resources/db/migration`
