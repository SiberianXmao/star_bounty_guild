# File Service

Централизованный сервис файлов. Проверяет изображения, сохраняет их в MinIO и возвращает публичные URL вида `/media/...`.

Сервис не хранит доменные связи. Например, `avatar_url` остаётся в `user-service`, а `image_url` планеты остаётся в `dictionary-service`.

## Ответственность

- JPEG/PNG изображения до 5 MB и до 4096x4096 пикселей;
- пространства файлов `avatars`, `planets`, `sectors`, `factions`;
- загрузка и удаление объектов в bucket `bounty-media`;
- internal API для других сервисов;
- health-check и метрики на management-порту `9086`.

## API

Внутренний префикс: `/internal/v1/files`.

Все запросы защищены заголовком `X-Internal-Token` и выполняются другими сервисами через Feign.

## Карта пакетов

| Пакет | Назначение |
| --- | --- |
| `common` | Общие ошибки и API response helpers |
| `config` | Настройки internal auth и storage properties |
| `domain` | Модели/типы файлового домена |
| `internal` | Internal REST API и проверка токена |
| `service` | Сервисные сценарии upload/delete |
| `storage` | Низкоуровневая работа с MinIO |
| `web` | REST-контроллеры file API |

## Запуск

```powershell
docker compose up -d --build minio minio-init file-service
```

- internal port: `8086`
- management port: `9086`
- MinIO Console: http://localhost:9001
