# QA API Training

Учебный Spring Boot API и набор API-тестов для практики Java, REST Assured, JUnit 5, тест-дизайна и основ backend-разработки.

## Зачем этот проект

Проект имитирует небольшой сервис объявлений: пользователь регистрируется, получает JWT при login и управляет своими объявлениями. Он нужен, чтобы тренировать не только отдельные HTTP-запросы, но и реальные QA-сценарии: авторизацию, ownership, cleanup тестовых данных, error contract и concurrency.

В проекте я практикую:

- API-тесты на Java с JUnit 5 и Rest Assured;
- JWT-аутентификацию и проверку защищённых endpoint'ов;
- security-сценарии IDOR/BOLA: пользователь не может удалить чужой item;
- cleanup созданных item через `try/finally` и `@AfterEach`;
- concurrency-тесты через `ExecutorService`, `CountDownLatch`, `Callable` и `Future`;
- PostgreSQL, Flyway, Docker Compose и OpenAPI.

## Быстрый старт

Нужны JDK 17 (рекомендуется) или JDK 21, Maven и Docker. В проекте установлен Java release 17; не запускай тесты на experimental JDK 26: текущий Mockito в Spring Boot 3.4 не умеет подключать там тестовый Java agent.

```bash
docker compose up -d
mvn spring-boot:run
```

После запуска: Swagger UI — `http://localhost:8080/swagger-ui.html`, OpenAPI — `http://localhost:8080/v3/api-docs`.

Для проверки компиляции без запуска тестов: `mvn -DskipTests test-compile`.

API-тесты отправляют HTTP-запросы в отдельно запущенное приложение на `http://localhost:8080`. После запуска приложения можно выполнить только API-сценарии:

```bash
mvn -Dtest=RegistrationApiTest,ItemApiTest,ItemSecurityApiTest,RegistrationConcurrencyTest,CreateItemConcurrencyTest test
```

Для локальной unit-практики без приложения и PostgreSQL: `mvn -Dtest=ItemValidatorPracticeTest test`. Общий запуск включает Selenium-тесты; `SauceDemoPurchaseUiTest` пока содержит намеренный `fail` и не завершён.

## Контракт

| Endpoint | Доступ | Успех | Основные ошибки |
| --- | --- | --- | --- |
| `POST /api/v1/auth/register` | public | `201` | `400`, `409` |
| `POST /api/v1/auth/login` | public | `200` | `400`, `401` |
| `POST /api/v1/items` | Bearer JWT | `201` | `400`, `401` |
| `GET /api/v1/items/{id}` | Bearer JWT | `200` | `401`, `404` |
| `PUT /api/v1/items/{id}` | owner | `200` | `400`, `401`, `404` |
| `DELETE /api/v1/items/{id}` | owner | `204` | `401`, `404` |

У чужого пользователя `PUT` и `DELETE` возвращают `404`, а не `403`: так API не раскрывает существование чужого ресурса. `sellerId` не принимается от клиента — владелец берётся из JWT; это защита от IDOR/mass assignment.

### Примеры JSON

```json
// POST /api/v1/auth/register
{"username":"alice","password":"password123"}

// POST /api/v1/items
{"name":"Keyboard","price":5000,"statistics":{"contacts":0,"likes":0,"viewCount":0}}
```

Формат ошибки всегда: `timestamp`, `status`, `code`, `message`, `path`.

## Архитектура

`controller → service → repository → PostgreSQL`. DTO находятся в `api`, JPA-сущности — в `user` и `item`; они не смешаны. Общая HTTP-безопасность живёт в `security`, а не в контроллерах. Схема БД создаётся миграцией Flyway.

### Архитектура API-тестов

- `config/RequestSpecFactory` задаёт общие настройки запросов: адрес приложения и JSON.
- `client/AuthApi` и `client/ItemsApi` отправляют запросы и возвращают полный `Response`. Статусы и содержимое ответа проверяются в тестах. `AuthApi.login` — вспомогательный метод успешного входа с проверкой `200`; `loginResponse` позволяет проверять полный ответ.
- `data/UserDataFactory` создаёт пользователя с уникальным username; `ItemDataFactory` предоставляет готовые варианты объявлений через `ItemDataBuilder`.
- `ItemDataBuilder` позволяет комбинировать имя, цену и статистику. Значения по умолчанию хранятся в builder; фабрика и builder допускают некорректные данные для негативных тестов.
- Ответ создания десериализуется в `ItemResponse` через `.as(ItemResponse.class)`. В `ItemApiTest` поля сравниваются с запросом, а UUID сохраняется для очистки.

Каждый тест готовит собственные данные. В конкурентной регистрации один пользователь создаётся до запуска потоков и используется в обоих запросах.

## Что писать самому

Задания и стартовые сценарии находятся в [learning/DAY_01_REST.md](learning/DAY_01_REST.md), [learning/DAY_02_JUNIT.md](learning/DAY_02_JUNIT.md) и [src/test/java](src/test/java). Создавай свои API-тесты в `src/test/java/ru/training/qa/api` — например `RegistrationApiTest`, `ItemsApiTest`, `SecurityApiTest`.

Рекомендуемый порядок: сначала позитивный register → login → create item, потом валидация, error contract, ownership, cleanup в `@AfterEach`, и лишь затем concurrency.

## Реализованные учебные сценарии

- регистрация и login с извлечением JWT;
- повторная регистрация одного username: `409` с проверкой контракта ошибки;
- создание item авторизованным пользователем и cleanup;
- запрет удаления чужого item: пользователь B получает `404`, а item пользователя A остаётся доступен владельцу;
- две одновременные регистрации одного username: один запрос получает `201`, второй — `409`;
- два одновременных создания item: оба запроса получают `201`, каждому item назначается собственный UUID, затем оба удаляются.

## Важно

`docker-compose.yml` содержит только локальные учебные credentials PostgreSQL. Настоящие токены, production-пароли и `.env` в Git не добавляются.
