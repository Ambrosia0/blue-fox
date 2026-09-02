# Community Service

A microservice for managing communities in the blog platform. Handles community lifecycle, user moderation, role-based access control, and community following.

## Data Ownership

- **Communities** — user-created groups with profiles, avatars, and access types (public/private)
- **Community Bans** — user bans within specific communities, with expiry dates
- **Roles/Scopes** — role-based permissions assigned to users within communities

## Responsibilities

### Community Management
- Providing a REST API for creating, editing, and deleting communities;
- Managing user roles and privileges within communities (scopes system);
- Implementing community moderation mechanisms (ban/unban users);
- Handling community avatars via S3 storage integration;
- Supporting public and private communities (with follow requests for private ones).

### Community Following
- Allowing users to follow/unfollow communities;
- Managing follow requests for private communities;
- Tracking and aggregating follow counts via Kafka Streams (exactly-once processing).

### Search & Discovery
- Community search via Elasticsearch (full-text search, filtering);
- Slug-based community lookup;
- Pagination and sorting support.

## Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL (via Spring Data JDBC)
- **Search**: Elasticsearch
- **Cache**: Redis
- **Message Broker**: Kafka (events + Kafka Streams with exactly-once semantics)
- **gRPC**: Inter-service communication
- **Auth**: OAuth2 / JWT (Keycloak)
- **Storage**: S3-compatible (for community avatars)
- **DB Migrations**: Liquibase

## API Endpoints

### Public (`/api/public/community`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/{slug}` | Get community details by slug |
| `GET` | (root) | Search communities |

### User (`/api/user/community`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | (root) | Create a new community |
| `PATCH` | `/{id}` | Edit community info |
| `PUT` | `/{id}/avatar` | Upload community avatar |
| `POST` | `/{id}/avatar/{avatarId}` | Confirm avatar upload |
| `PUT` | `/{id}/scopes` | Edit user scopes/roles in community |
| `GET` | `/{id}/scopes` | Get all users' scopes for a community |
| `GET` | `/{id}/me/scopes` | Get current user's scopes for a community |
| `GET` | `/scopes` | List all available scopes |
| `POST` | `/slugcheck` | Check if a slug is already taken |
| `POST` | `/{id}/ban/{userId}` | Ban a user from community |
| `DELETE` | `/{id}/ban/{userId}` | Unban a user from community |

### Admin (`/api/admin/community`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | (root) | List all communities (paginated) |
| `PATCH` | `/{id}` | Edit community info |
| `DELETE` | `/{id}` | Delete a community |
| `PUT` | `/{id}/avatar` | Upload community avatar |
| `POST` | `/{id}/avatar/{avatarId}` | Confirm avatar upload |
| `PUT` | `/{id}/scopes` | Edit community scopes |

## Kafka Topics

| Topic | Direction | Description |
|-------|-----------|-------------|
| `blog.community.ban` | Outbound | Published on user ban/unban events |
| `blog.community` | Outbound | Published on community creation/update/delete events |
| `blog.community.follow` | Inbound/Outbound | Follow/unfollow events + Kafka Streams aggregation |

## Inter-Service Communication

### gRPC Channels
- **community-channel** (port 9090) — provides community data to other services (called by comment-service, content-service)

### Kafka Events (Outbound)
- Community lifecycle events (creation, update, deletion)
- Ban/unban events
- Follow events

## Configuration

Key environment variables:

| Variable | Description |
|----------|-------------|
| `DB_URL` | PostgreSQL connection URL |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |
| `REDIS_URL` | Redis connection URL |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker addresses |
| `ELASTIC_URIS` | Elasticsearch cluster URIs |
| `OIDC_ISSUER_URL` | Keycloak issuer URL for JWT validation |
| `S3_ENDPOINT` | S3-compatible storage endpoint |
| `S3_REGION` | S3 region |
| `S3_ACCESS_KEY` / `S3_SECRET_KEY` | S3 credentials |
| `S3_BUCKET` | Public bucket name for avatars |
| `FILE_ENDPOINT` | File service endpoint |

## Profiles

- **test** — Disables Kafka Streams auto-startup for integration testing
- **es-disabled** — Disables Elasticsearch (for environments without search capability)

## Building & Running

```bash
# Build
./gradlew community-service:build

# Run tests
./gradlew community-service:test

# Docker image
./gradlew community-service:bootBuildImage

# Native image (without Elasticsearch)
./gradlew community-service:bootBuildImage -Pnative -Pspring.active.profiles=es-disabled
```