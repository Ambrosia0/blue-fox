# Description

A real-time notification and event delivery microservice built with Quarkus. It aggregates events from various domain services via Apache Kafka, maintains synchronized database projections, and delivers real-time updates to clients through Server-Sent Events (SSE).

## Kafka Consumers

Consumers are split into 2 groups:

### Persistence Consumers

These consumers poll Kafka topics, deserialize events (Protobuf), and persist the resulting state changes to PostgreSQL. They maintain the service's read models (projections).

| Consumer | Topics | Description |
|----------|--------|-------------|
| `NotificationConsumer` | `blog.notification` | Notification events → community, user, targeted notifications |
| `CommunityConsumer` | `blog.community` | Community CRUD events → communities table |
| `CommunityBanConsumer` | `blog.community.ban` | Community ban/unban events → bans table |
| `CommunityFollowConsumer` | `blog.community.follow` | Community follow/unfollow events → community follows table |
| `PostConsumer` | `blog.post` | Post CRUD events → posts table |
| `UserConsumer` | `blog.user` | User CRUD events → users table |
| `UserFollowConsumer` | `blog.user.follow` | User follow/unfollow events → user follows table |

### SSE Delivery Consumers

These consumers poll notification and activity events from Kafka and deliver them in real-time to connected SSE clients via the Vert.x EventBus. Topics include:

| Channel | Topic | Auto Offset Reset |
|---------|-------|--------------------|
| `notification-event` | `blog.notification` | earliest |
| `comment-channel` | `blog.comment` | latest |
| `post-like-channel` | `blog.post.like` | latest |
| `comment-like-channel` | `blog.comment.like` | latest |
| `post-view-channel` | `blog.post.view.aggregation` | latest |

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/me/notification` | Get notifications for authenticated user |
| `GET` | `/api/v1/me/notification/new` | Count new (unread) notifications |

## SSE Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/events` | Real-time events for authenticated user (requires OIDC auth) |
| `GET` | `/api/v1/public/post/{postId}/events` | Real-time events for a specific post (public) |

Both SSE endpoints include automatic heartbeat pings (default interval: 15s) to keep connections alive.

## Technology Stack

- **Java 25**
- **Quarkus 3.32.3** — Supersonic Subatomic Java Framework
- **PostgreSQL** — Reactive PostgreSQL client (Vert.x)
- **Apache Kafka** — SmallRye Reactive Messaging
- **Protobuf** — Cross-service event serialization (proto-gen module)
- **Flyway** — Database migrations
- **OIDC** — Authentication

## Project Structure

```
src/main/java/org/ambrosia/notification_service/
├── core/                      # Core utilities & configuration
│   ├── ActorFactory.java
│   ├── AppConfiguration.java
│   └── policy/                # Policy pattern implementation
├── event/                     # SSE event delivery
│   ├── api/                   # EventController, PostEventController
│   ├── application/           # UserEventRegistry, PostEventRegistry, etc.
│   ├── domain/                # Domain interfaces (UserFollowRepository, etc.)
│   └── infrastructure/        # Persistence entities & adapters
├── kafka/                     # Kafka consumers & events
│   ├── consumer/              # All Kafka consumer classes
│   └── event/                 # Kafka event DTOs
├── notification/              # Notification management
│   ├── api/                   # NotificationController, DTOs
│   ├── application/           # Query services
│   └── infrastructure/        # Notification persistence
└── projections/               # Database projections
    └── infrastructure/        # Projections entities & repositories
```

## Proto-Generated Modules

The `proto-gen` subproject generates Java classes from Protobuf definitions for inter-service communication:

| Service | Package |
|---------|---------|  
| Comment Service | `com.ambrosia.comment_service.kafka_events` |
| Community Service | `com.ambrosia.community_service.kafka_events` |
| Content Service | `com.ambrosia.content_service.kafka_events` |
| Profile Service | `com.ambrosia.profile_service.kafka_events` |
| Notification Service | `org.ambrosia.notification_service.kafka_events` |

## Running the Application

### Prerequisites

- Java 25
- Docker (for Kafka, PostgreSQL dev services)

### Dev Mode

```shell script
./gradlew quarkusDev
```

The application starts at `http://localhost:8080`. The Quarkus Dev UI is available at `http://localhost:8080/q/dev/`.

### Packaging and Running

#### Build and run uber jar

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
java -jar build/*-runner.jar
```

#### Build container

Build uber jar:

```shell script
./gradlew build '-Dquarkus.package.jar.type=uber-jar' '-Dquarkus.container-image.build=false' -x test
```

Build the application container (requires docker):

```shell script
./gradlew build
```

Build native executable container (requires docker):

```shell script
./gradlew build '-Dquarkus.native.enabled=true' -x test
```

> **Note:** Native builds use container runtime (Docker) by default as configured in `application.properties`.

## Configuration

Key configuration properties are defined in `src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `app.heartbitInterval` | `PT15S` | SSE heartbeat interval |
| `app.leaseRefreshInterval` | `PT2M` | Lease refresh interval |
| `quarkus.http.port` | `8080` | HTTP server port |
| `quarkus.flyway.migrate-at-start` | `true` | Auto-run migrations on startup |

### Production Properties

| Property | Environment Variable | Description |
|----------|---------------------|-------------|
| `%prod.quarkus.oidc.auth-server-url` | `OIDC_ISSUER_URL` | OIDC issuer URL |
| `%prod.mp.messaging.connector.smallrye-kafka.bootstrap.servers` | `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers |

## Documentation

Additional documentation is available in the `docs/` directory:

- [`event-processing.md`](docs/event-processing.md) — Kafka consumer processing workflow
- [`event-serve.md`](docs/event-serve.md) — SSE event delivery flow
