# Monitoring

В проекте используется связка Spring Boot Actuator, Micrometer, Prometheus и
Grafana. Она показывает состояние сервисов и числовые характеристики приложения
во времени: нагрузку, ошибки, задержки, память JVM, CPU и соединения с БД.

## Как проходит метрика

```text
Spring Boot
  -> Micrometer измеряет HTTP, JVM, HikariCP и другие компоненты
  -> Actuator публикует /actuator/prometheus
  -> Prometheus опрашивает endpoint каждые 15 секунд
  -> Prometheus сохраняет временные ряды в своей TSDB
  -> Grafana выполняет PromQL-запросы к Prometheus
  -> dashboard отображает графики и показатели
```

Prometheus работает по pull-модели: не сервис отправляет данные, а Prometheus сам
периодически приходит за актуальными значениями. Если endpoint недоступен,
метрика `up` становится `0`, и это сразу видно на dashboard.

## Роли компонентов

### Actuator

Spring Boot Actuator добавляет служебные endpoint-ы приложения. В проекте
открыты только:

- `/actuator/health` — состояние сервиса;
- `/actuator/info` — базовая информация;
- `/actuator/prometheus` — метрики в формате Prometheus.

Management endpoint-ы работают на отдельных внутренних портах `9080-9084` и `9086`.
Они не проходят через gateway и не публикуются на host-машину.

### Micrometer

Micrometer — фасад метрик для Java, похожий по назначению на SLF4J для логов.
Spring автоматически регистрирует JVM, HTTP, HikariCP и process metrics, а
Prometheus registry преобразует их в нужный текстовый формат.

Каждая метрика получает label `application`, поэтому один Prometheus может
хранить данные всех пяти сервисов и разделять их в запросах.

### Prometheus

Prometheus опрашивает шесть targets из
[`prometheus/prometheus.yml`](prometheus/prometheus.yml) каждые 15 секунд.
Данные сохраняются в Docker volume на 15 дней.

Интерфейс: http://localhost:9090

Страница targets: http://localhost:9090/targets

Пример PromQL-запроса доступности сервисов:

```promql
up{job="spring-services"}
```

Пример количества HTTP-запросов в секунду:

```promql
sum(rate(http_server_requests_seconds_count[2m])) by (application)
```

### Grafana

Grafana сама не собирает метрики и не является их основным хранилищем. Она
подключается к Prometheus как к datasource, выполняет PromQL и строит панели.

При первом запуске автоматически создаются:

- datasource `Prometheus`;
- папка `Bounty Guild`;
- dashboard `Bounty Guild - Services Overview`.

Dashboard содержит:

- количество доступных сервисов;
- HTTP requests per second;
- долю ответов 5xx;
- p95 HTTP latency;
- использование JVM heap и CPU;
- активные HikariCP connections;
- время пауз garbage collector.

Интерфейс: http://localhost:3001

Логин: `admin`. Пароль задается переменной `GRAFANA_ADMIN_PASSWORD` в `.env`.

## Запуск и остановка

Monitoring запускается вместе со всем проектом:

```powershell
docker compose up -d --build
```

Проверка:

```powershell
docker compose ps prometheus grafana
```

Остановить только monitoring, сохранив его данные:

```powershell
docker compose stop prometheus grafana
```

Снова запустить:

```powershell
docker compose start prometheus grafana
```

`docker compose down` удалит контейнеры, но оставит named volumes. Команда
`docker compose down -v` удалит также метрики Grafana/Prometheus и все данные
PostgreSQL, Redis и MinIO, поэтому использовать `-v` нужно осознанно.

## Чего здесь пока нет

Эта связка отвечает за metrics. Она не собирает централизованные логи и traces.
Если проект будет развиваться дальше, естественное продолжение:

- Loki + Grafana Alloy для логов;
- OpenTelemetry + Tempo для distributed tracing;
- Alertmanager для уведомлений по правилам Prometheus.
