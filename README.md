# Overview

Backend for a blog platform. Architecture — microservices.

## Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **DB Migrations**: Liquibase
- **Message Broker**: Kafka (events + Kafka Streams)
- **Cache**: Redis
- **Databases**: PostgreSQL, Elasticsearch
- **Authentication**: OAuth2 / JWT (Keycloak)
- **Inter-service Communication**: gRPC, Kafka Events
- **File Storage**: S3-compatible
- **API Contract**: Protobuf (for inter-service communication and Kafka events)

## Project Structure

The project consists of the following microservices:

### User

- **`profile-service`** — Central user profile management system: synchronization with Keycloak, user data store, presence (online status), blacklists, local user projections. Provides a gRPC API for checking user existence and validating blacklists for other services.

### Content

- **`content-service`** — Post management (WYSIWYG / TipTap), likes, attachments, and user subscriptions. Post search (Elasticsearch Or PostgreSQL (with RUM index)), view count aggregation (Kafka Streams) and likes (Redis), personalized feeds based on subscriptions.

- **`comment-service`** — Hierarchical comments (n-ary tree), comment likes, attachments (two-step upload flow). Local projections of posts and communities for decoupled communication. Support for cursor-based pagination and sorting.

- **`report-service`** — Content moderation: user-submitted reports, report reasons with i18n translations, administrative management of report statuses. Local read-only projections of all platform entities for report target validation. Exactly-once processing via the `processed_events` ыtable.

### Communities

- **`community-service`** — Community management (public / private), roles and scopes system, user ban/unban, community subscriptions. Search via Elasticsearch. Subscriber aggregation via Kafka Streams.

### Infrastructure

- **`api-gateway`** — API gateway for incoming external HTTP requests, routing, authentication
- **`notification-service`** — User notification system, subscription to platform events
- **`frontend`** — Platform frontend application

## Additional Components

- **Kafka** — asynchronous communication between services
- **PostgreSQL** — primary database
- **Elasticsearch** — search and indexing
- **Redis** — caching
- **Keycloak** — authentication and authorization

## Documentation

- [Kafka Topics](docs/kafkaTopics.md) — Kafka topics description

## Running

### Local Run

```bash
# Run all services via docker-compose
docker compose -f docker-compose-dev.yaml up -d
```

### Building the Project

#### Building the postgres image with RUM index
```bash
cd /infra/postgres
docker build -t postgres-18-rum .
```

#### Building the keycloak image with Kafka SPI
```bash
cd /infra/keycloak
docker build -t keycloak-kafka .
```
#### Building the frontend application
```bash
cd frontend
docker build -t frontend-app .
```

```bash
# Build all services
./gradlew build

# Build a specific service
./gradlew community-service:build

# Test a specific service
./gradlew community-service:test
```

#### Image Build without Elasticsearch
```bash
./gradlew bootBuildImage -Pspring.active.profiles=es-disabled
```

## Deployment

K8s deployment files are located in the `deploys/` directory. Use the corresponding YAML files for deployment.
