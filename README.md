# Finance Bot

[![Build](https://github.com/SlavikN14/telegram-bot-finance/actions/workflows/build.yaml/badge.svg)](https://github.com/SlavikN14/telegram-bot-finance/actions/workflows/build.yaml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)

Telegram bot for tracking personal finances. Pulls live currency exchange rates from the **[Monobank Open API](https://api.monobank.ua/bank/currency)** and lets users log income and expenses, monitor balance, and view reports — all from chat.

## Features

- Live currency exchange rates from Monobank (cached in Redis to avoid rate-limits).
- Income & expense tracking with per-user history.
- Multi-language UI (English, Ukrainian).
- Event-driven microservices: `telegram-bot` ↔ `finance-service` over gRPC / NATS / Kafka.

## Architecture

```
                +-----------------+
                |   Telegram API  |
                +--------+--------+
                         |
                         v
    +----------------------------+        gRPC / NATS / Kafka       +-----------------------------+
    |        telegram-bot        | <-------------------------------> |       finance-service       |
    |  (user interaction, i18n,  |                                   | (DDD + hexagonal, domain    |
    |   Redis cache, Monobank)   |                                   |  logic, persistence)        |
    +------------+---------------+                                   +--------------+--------------+
                 |                                                                  |
                 v                                                                  v
           +-----------+                                                     +-------------+
           |   Redis   |                                                     |   MongoDB   |
           +-----------+                                                     +-------------+
```

- **`telegram-bot`** — user-facing edge service. Handles Telegram updates, caches currency data in Redis, calls Monobank.
- **`finance-service`** — domain service. Owns persistence and business rules; structured with DDD + hexagonal architecture (domain module inside).
- **`shared-api`** — shared Protobuf / gRPC contracts and Kafka Avro schemas consumed by both services.

## Project Structure

```
telegram-bot/             # Edge service: Telegram API, i18n, Redis cache, Monobank client
finance-service/          # Domain service, split into hexagonal layers:
  ├── domain/             # Entities, value objects, domain services — zero frameworks
  ├── application/        # Use cases, input/output ports — depends on domain only
  ├── infrastructure/     # Adapters: Mongo, NATS, mappers, gRPC — Spring lives here
  └── bootstrap/          # Spring Boot entrypoint, wiring, resources, Dockerfile jar
shared-api/               # Shared gRPC / Protobuf / Kafka schemas
docker-compose.yaml       # Local infra (Mongo, Redis, NATS, Kafka, Schema Registry)
```

Dependency direction inside `finance-service/`:
`bootstrap → infrastructure → application → domain`. Domain and application know nothing about Spring, Mongo, or NATS — that is the point of the hexagon.

## Prerequisites

- JDK 17
- Docker + Docker Compose
- Telegram bot token from [@BotFather](https://t.me/BotFather)

## Getting Started

1. Clone the repo.
2. Provide the bot token via env vars (recommended), JVM options, or `telegram-bot/src/main/resources/application.yaml`:

   ```yaml
   bot:
     token: your_bot_token
     username: your_bot_name
   ```

3. Launch everything:

   ```bash
   ./run-docker-compose.sh
   ```

   This builds the Gradle artifacts and starts all services + infrastructure via Docker Compose.

### Run locally without Docker

Start MongoDB, Redis, NATS, and Kafka yourself, then:

```bash
./gradlew :telegram-bot:bootRun
./gradlew :finance-service:bootRun
```

## Exposed Ports

| Service         | Port                  |
|-----------------|-----------------------|
| telegram-bot    | `8080`, `9091` (gRPC) |
| finance-service | `8082`                |
| MongoDB         | `27017`               |
| Redis           | `6379`                |
| NATS            | `4222`                |
| Kafka           | `9092`                |
| Schema Registry | `8081`                |

## Testing

```bash
./gradlew test        # unit + integration tests
./gradlew detekt      # static analysis
```

## Usage

Open Telegram, find your bot by username, then:

- `/start` — initialize and show the menu.
- Pick a currency to fetch its current Monobank rate.
- Log income or expenses to update your balance.
- Switch language between English and Ukrainian.

## License

MIT.