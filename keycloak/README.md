# Keycloak

Конфигурация Keycloak для Bounty Guild.

## Содержит

- realm `bounty-guild`;
- роли `ADMIN`, `MODERATOR`, `CLIENT`, `HUNTER`;
- public client `bounty-frontend` с PKCE;
- redirect URI для frontend.

Realm import находится в `realm/bounty-guild-realm.json`. Demo-пользователи и пароли в файл не добавляются.

## Запуск

```powershell
docker compose up -d keycloak-postgres keycloak
```

Admin console: http://localhost:8085

Логин bootstrap-admin задаётся в compose, пароль берётся из `KEYCLOAK_ADMIN_PASSWORD` в корневом `.env`.

Изменение realm JSON не обновляет уже импортированный realm автоматически. Для существующей базы настройки меняются через Admin Console.
