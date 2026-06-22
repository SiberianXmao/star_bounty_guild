# Profiles Service

Сервис профилей участников гильдии.

## Отвечает за

- профили заказчиков;
- профили охотников;
- навыки и доступность охотников;
- рейтинг, надёжность и статистику выполненных заказов;
- публичный каталог охотников.

Сервис хранит данные в базе `bounty_profiles`. Пользователей получает из user-service, справочные данные - из dictionary-service.

## API

- публичный префикс: `/api/v1/profiles`;
- внутренний префикс: `/internal/v1/profiles`.

## Запуск

```powershell
docker compose up -d --build profiles-postgres dictionary-service user-service profiles-service
```

- сервис: http://localhost:8083
- Swagger: http://localhost:8083/swagger-ui.html
- миграции: `src/main/resources/db/migration`
