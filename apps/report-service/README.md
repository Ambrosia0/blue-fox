# Report Service

A microservice for managing content moderation reports, report reasons with i18n translations, and read-only projections of platform entities (posts, comments, users, communities) used for report target validation.

## Data Ownership

- **Reports** — user-submitted content reports with status tracking (OPEN/CLOSE), reason classification, and resolution attribution
- **Report Reasons** — predefined moderation categories (e.g., `SEXUAL_CONTENT`, `VIOLENCE`, `COPYRIGHT`, `HARASSMENT`, `OTHER`) with i18n translations
- **Post Projections** — local read-only copies of post IDs for validating report targets
- **Comment Projections** — local read-only copies of comment IDs for validating report targets
- **User Projections** — local read-only copies of user IDs for validating report targets
- **Community Projections** — local read-only copies of community IDs for validating report targets
- **Processed Events** — idempotency tracking table to ensure exactly-once event processing

## Responsibilities

### Content Moderation Management
- Providing a REST API for users to submit reports against posts, comments, users, and communities
- Validating report targets against local projections before creating reports
- Providing an admin API for listing, filtering, and closing moderation reports
- Managing report reasons and their i18n translations (create, read, delete)

### Projection Synchronization (Read-Only)
- Consuming events from Kafka to maintain local projections of platform entities
- Using `processed_events` table for exactly-once processing guarantees
- Providing existence checks for report target validation during report creation

### Report Reason Management
- Maintaining a catalog of moderation reason codes
- Supporting i18n translations for report reasons via upsert and delete operations
- ISO 639-1 language code validation for translation endpoints

## Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL (via Spring Data JDBC with JDBC dialect)
- **Cache**: Redis (1-minute TTL cache for report reasons)
- **Message Broker**: Kafka (event consumers for projection sync)
- **Auth**: OAuth2 / JWT (Keycloak)
- **DB Migrations**: Liquibase

## Configuration

### Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection string | *required* |
| `DB_USER` | PostgreSQL username | *required* |
| `DB_PASSWORD` | PostgreSQL password | *required* |
| `REDIS_URL` | Redis connection URL | *required* |
| `OIDC_ISSUER_URL` | OAuth2/OIDC issuer URL (Keycloak) | *required* |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers | *required* |

### Profiles

| Profile | Description |
|---|---|
| `test` | Enables debug logging, enables Liquibase |

### Kafka Configuration

| Property | Value |
|---|---|
| Consumer Group | `report-service` |
| Ack Mode | `batch` |
| Key Deserializer | `StringDeserializer` |
| Value Deserializer | `ByteArrayDeserializer` (Protobuf) |
| Auto-creation (dev/test) | 2 partitions, 1 replica per topic |

### Error Handling

- **Dead Letter Topic Recovery**: `DeadLetterPublishingRecoverer` with 0 retries, immediate failure
- **Serialization Error Handler**: Propagates deserialization errors for visibility (Protobuf parsing)

## API Endpoints

### User (Authenticated)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/user/report/reason` | Get report reasons filtered by language (default: `en`) |
| `POST` | `/api/user/report` | Submit a new report against a target (post, comment, user, or community) |

### Admin (Admin Role Required)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/report` | List reports with filtering (status, target type) and pagination |
| `GET` | `/api/admin/report/reason` | Get all report reasons with i18n data |
| `POST` | `/api/admin/report/{id}/status` | Close a moderation report |
| `POST` | `/api/admin/report/reason/{id}/translation` | Create or update a report reason translation (upsert) |
| `GET` | `/api/admin/report/reason/{id}/translation` | Get all translations for a report reason |
| `DELETE` | `/api/admin/report/reason/{id}/translation/{lang}` | Delete a report reason translation by language |

## Kafka Topics

### Inbound Events (Consumed)

| Topic | Event Type | Description |
|---|---|---|
| `blog.user` | `UserEvent` (protobuf) | User creation events -> maintains user projections |
| `blog.post` | `PostEvent` (protobuf) | Post creation/deletion events -> maintains post projections |
| `blog.comment` | `CommentEvent` (protobuf) | Comment creation/deletion events -> maintains comment projections |
| `blog.community` | `CommunityEvent` (protobuf) | Community creation/deletion events -> maintains community projections |
