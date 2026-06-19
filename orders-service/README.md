# Orders Service

Сервис контрактов гильдии.

## Отвечает за

- создание и публикацию заказов;
- доску публичных контрактов и фильтрацию;
- заявки охотников;
- персональные предложения;
- назначение охотника и переходы статусов;
- transactional outbox и публикацию событий в Kafka.

Сервис хранит данные в базе `bounty_orders`. Через Feign обращается к user-service, profiles-service и dictionary-service.

При принятии заявки публикуется событие:

```text
bounty.applications.application-accepted.v1
```

## API

- `/api/v1/orders` - заказы;
- `/api/v1/applications` - заявки.

## Запуск

```powershell
docker compose up -d --build orders-postgres kafka user-service dictionary-service profiles-service orders-service
```

- сервис: http://localhost:8084
- Swagger: http://localhost:8084/swagger-ui.html
- миграции: `src/main/resources/db/migration`
