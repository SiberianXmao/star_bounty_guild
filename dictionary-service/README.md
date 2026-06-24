# Dictionary Service

Сервис справочных данных Bounty Guild.

## Отвечает за

- фракции;
- секторы и планеты;
- валюты;
- категории заказов;
- навыки охотников.

Сервис хранит данные в базе `bounty_dictionary`. Другие сервисы получают справочники через REST/Feign. Изображения загружаются через `file-service`, а здесь хранится только их URL.

Публичные списки справочников кэшируются в Redis на 6 часов. После создания новой записи соответствующий кэш очищается автоматически.

## API

- публичный префикс: `/api/v1/dictionary`;
- внутренний префикс: `/internal/v1/dictionary`.

Internal API защищён заголовком `X-Internal-Token`.

## Запуск

```powershell
docker compose up -d --build file-service dictionary-postgres redis dictionary-service
```

- сервис: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui.html
- миграции: `src/main/resources/db/migration`
