# Exchange Rates

REST API для загрузки курсов валют из Fixer.io и хранения данных в PostgreSQL.
Приложение написано на Java 17 с использованием Spring Boot 3.3.4,
Spring Data JPA и Liquibase. Для сборки используется Gradle Wrapper 8.10.2.

## Требования

- JDK 17.
- Docker для запуска PostgreSQL либо доступная база PostgreSQL.
- Доступ к интернету для первой загрузки Gradle и зависимостей.
- Собственный ключ Fixer.io для загрузки актуальных курсов.

## Сборка и тесты

Из корня репозитория:

```bash
bash gradlew build --no-daemon --max-workers=2
```

Только тесты:

```bash
bash gradlew test --no-daemon --max-workers=2
```

Тесты используют H2 и подменяют вызовы Fixer.io; действующий API-ключ и
PostgreSQL для них не нужны. HTML-отчёт находится в
`build/reports/tests/test/index.html`.

## Локальный запуск

Следующий пример запускает отдельную базу для разработки. Задайте пароль
в переменной окружения, не добавляя его в Git:

```bash
read -rsp 'Пароль локального PostgreSQL: ' POSTGRES_PASSWORD
echo
export POSTGRES_PASSWORD

docker run -d --name exchangerates-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD \
  -e POSTGRES_DB=exchangerates \
  -p 127.0.0.1:5432:5432 \
  -v exchangerates-postgres-data:/var/lib/postgresql/data \
  postgres:16
```

Дождитесь готовности базы:

```bash
docker exec exchangerates-postgres pg_isready -U postgres -d exchangerates
```

Настройте подключение и запустите приложение:

```bash
export SPRING_DOCKER_COMPOSE_ENABLED=false
export SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5432/exchangerates
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"

read -rsp 'API-ключ Fixer.io: ' FIXER_APIKEY
echo
export FIXER_APIKEY
export FIXER_URL='https://data.fixer.io/api/latest?access_key={API_KEY}'

bash gradlew bootRun --no-daemon --max-workers=2
```

Liquibase применяет миграции при запуске. Приложение слушает порт `8080`.
Автоматический запуск Compose отключён, поскольку база запускается отдельно.
При повторном запуске существующего контейнера используйте
`docker start exchangerates-postgres`. Пароль должен соответствовать тому,
с которым был инициализирован том базы.

Для запуска без внешнего провайдера вместо настроек Fixer.io задайте:

```bash
export FIXER_APIKEY=unused-local-development
export FIXER_URL=http://127.0.0.1:9/disabled
```

В таком режиме доступны запуск и чтение сохранённых данных; добавление валют
и обновление курсов требуют доступного Fixer.io. Значения доступа из
`application.yaml` следует переопределять своими переменными окружения.

## API

Базовый путь: `/api/currencyExchange`.

| Метод | Путь | Назначение |
| --- | --- | --- |
| GET | `/getAllCurrencies` | Коды валют в кеше приложения |
| GET | `/getByCurrencyCode?currencyCode=EUR` | Сохранённая валюта и её курсы; `204`, если валюта отсутствует |
| POST | `/addCurrency` | Загрузить курсы из Fixer.io и сохранить результат |

Примеры запросов:

```bash
curl --fail http://localhost:8080/api/currencyExchange/getAllCurrencies

curl --fail 'http://localhost:8080/api/currencyExchange/getByCurrencyCode?currencyCode=EUR'

curl --fail -X POST http://localhost:8080/api/currencyExchange/addCurrency \
  -H 'Content-Type: application/json' \
  -d '{"currencyCode":"EUR"}'
```

На пустой базе первый запрос возвращает `[]`. Список кодов строится из кеша,
поэтому он может отличаться от содержимого базы до чтения отдельных валют.
Параметр `currencyCode` не передаётся провайдеру как базовая валюта:
приложение сохраняет базу, возвращённую Fixer.io. Возможности внешнего API
зависят от тарифного плана. Планировщик обновляет валюты из кеша каждый час.

## Подготовленная облачная среда

В среде, подготовленной через Codex, зависимости и локальные помощники
находятся вне репозитория:

```bash
source /workspace/.setup/exchangerates/env.sh
cd /workspace/exchangerates
bash gradlew build --no-daemon --max-workers=2
bash /workspace/.setup/exchangerates/start.sh
```

`env.sh` подключает Java 17, кеш Gradle и сетевой прокси среды. `start.sh`
поднимает локальный PostgreSQL и запускает приложение с отключёнными
обращениями к Fixer.io. Если приложение уже запущено, повторный экземпляр
запускать не нужно. Эти помощники существуют только в подготовленной среде;
для обычной машины используйте инструкции локального запуска выше.
