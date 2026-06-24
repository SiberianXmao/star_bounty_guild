# File Service

Централизованный сервис файлов Bounty Guild. Он проверяет изображения, хранит их в MinIO и возвращает публичные URL вида `/media/...`.

## Ответственность

- JPEG/PNG изображения размером до 5 МБ и до 4096x4096 пикселей;
- пространства `avatars`, `planets`, `sectors`, `factions`;
- загрузка и удаление объектов в bucket `bounty-media`;
- метрики и health-check на management-порту `9086`.

Сервис не хранит доменные связи. Например, поле `avatar_url` остаётся в `user-service`, а `image_url` планеты остаётся в `dictionary-service`.

## API

Внутренний префикс: `/internal/v1/files`. Все запросы защищены заголовком `X-Internal-Token` и выполняются другими сервисами через Feign.

## Запуск

```powershell
docker compose up -d --build file-service
```
