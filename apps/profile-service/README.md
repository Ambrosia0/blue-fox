# Profile Service

A microservice for managing user profiles, presence, blacklists, and user projections in the blog platform. Acts as the central user data hub, synchronizing with the external Keycloak IdP and serving gRPC inter-service communication for user existence checks and blacklist validation.

## Data Ownership

- **Users** — user profiles, settings, and local projections synced from external IdP (Keycloak)
- **User Blacklists** — user-to-user blocking relationships
- **User Presence State** — real-time online/offline tracking via Kafka Streams

## Responsibilities

### User Profile Management
- Synchronizing user data with external Identity Provider (Keycloak);
- Storing additional user information not present in the external IdP;
- Managing user avatar uploads via S3-compatible storage;
- Providing REST API for user profile CRUD operations;
- Supporting username changes with history tracking;
- Managing user settings (notifications, privacy preferences).

### User Projection Synchronization
- Consuming and aggregating events from Kafka to maintain local user projections;
- Creating/updating/deleting local user records based on IdP events;
- Publishing user events to Kafka for other services to consume;
- Supporting lazy user creation for authenticated requests (when `app.profile-lazy-creation=true`).

### User Blacklist Management
- Providing REST API for managing user-to-user block lists;
- Exposing gRPC endpoint for inter-service blacklist validation.

### User Presence & Online Tracking
- Tracking user connect/disconnect events via Kafka Streams;
- Maintaining a real-time gauge of online users via Micrometer.

### gRPC Inter-Service Communication
- Serving user blacklist queries for other services (content-service, comment-service, community-service).

## Configuration

### Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection string | *required* |
| `DB_USER` | PostgreSQL username | *required* |
| `DB_PASSWORD` | PostgreSQL password | *required* |
| `REDIS_URL` | Redis connection URL | *required* |
| `ELASTIC_URIS` | Elasticsearch connection URI | *required* |
| `OIDC_ISSUER_URL` | OAuth2/OIDC issuer URL (Keycloak) | *required* |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers | *required* |
| `S3_ENDPOINT` | S3-compatible storage endpoint | *required* |
| `S3_REGION` | S3 region | `aws-global` |
| `S3_BUCKET` | S3 bucket name for avatars | `publicbucket` |
| `S3_ACCESS_KEY` | S3 access key | *required* |
| `S3_SECRET_KEY` | S3 secret key | *required* |
| `S3_PRESIGN_ENDPOINT` | S3 presign URL override | `${S3_ENDPOINT}` |
| `KC_REALM` | Keycloak realm | *required* |
| `KC_SECRET` | Keycloak client secret | *required* |
| `KC_CLIENT_ID` | Keycloak client ID | *required* |
| `KC_BASE_URL` | Keycloak base URL | *required* |
| `KC_KEY_ROTATION` | Keycloak auth key rotation time (min) | `5` |
| `S3_PUBLIC_BUCKET` | S3 public bucket name | `publicbucket` |

### Profiles

| Profile | Description |
|---|---|
| `test` | Disables Kafka Streams auto-startup, enables Liquibase, allows bean overriding |
| `es-disabled` | Disables Elasticsearch (for testing without ES), enables TRGM search context |

## API Endpoints

### Public (No Authentication)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/public/profile/{username}` | Get public profile by username (includes follow status if authenticated) |
| `POST` | `/api/public/profile/info` | Get batch user information by IDs (for inter-service calls) |
| `GET` | `/api/public/profile` | Search users by string (min 3, max 32 chars, returns up to 10 results) |

### User (Authenticated)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/me/profile` | Get current user's full profile |
| `PATCH` | `/api/me/profile/about` | Update user's about text (max 500 chars) |
| `PATCH` | `/api/me/profile/username` | Change username (8–32 chars, tracked in history) |
| `PATCH` | `/api/me/profile/name` | Update first/last name |
| `PUT` | `/api/me/profile/avatar` | Upload/update avatar (via presigned URL metadata) |
| `POST` | `/api/me/profile/avatar/{avatarId}` | Confirm avatar upload |
| `PUT` | `/api/me/profile/settings` | Update user settings (notifications, privacy) |
| `GET` | `/api/me/blacklist` | Get user's blacklist (paginated, 20 per page) |
| `POST` | `/api/me/blacklist/{blacklistedUser}` | Block a user |
| `DELETE` | `/api/me/blacklist/{userId}` | Unblock a user |

### Admin

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/admin/profile/{id}/ban` | Ban a user (sets `enabled=false`, syncs with Keycloak) |
| `POST` | `/api/admin/profile/{id}/unban` | Unban a user (sets `enabled=true`, syncs with Keycloak) |
| `GET` | `/api/admin/profile/` | Search and list users with filtering (paginated, 20 per page, default sort: DESC) |

## Kafka Topics

### Inbound Events (Consumed)

| Topic | Description |
|---|---|
| `blog.user.event` | User create/update/delete events from IdP admin service |
| `blog.user.aggregation` | Partial user data events (posts, comments) for local projections |
| `blog.user.follow.aggregation` | Partial follow data events for local projections |
| `blog.user.status` | User presence/connect/disconnect events |
| `blog.user.activity` | User activity events for enrichment |
| `blog.keycloak.event` | Keycloak user events (create/update) |
| `blog.keycloak.event.admin` | Keycloak admin events (ban/unban) |
| `blog.post.event` | Post lifecycle events |
| `blog.comment.event` | Comment lifecycle events |
| `blog.notification.aggr` | Notification aggregation events |
| `blog.user.follow` | User follow events |
| `blog.community.follow` | Community follow events |

### Outbound Events (Produced via Outbox)

| Topic | Event Type | Description |
|---|---|---|
| `blog.user` | `UserEvent` | User create/update/delete events published via Kafka Outbox |
| `blog.user.event` | `UserEvent` | User lifecycle events for other services |
| `blog.user.follow` | `UserFollowEvent` | User follow/unfollow events |
| `blog.user.status` | `ActivityEvent` | User presence/connect/disconnect events |
| `blog.user.aggregation` | `UserAggregation` | Aggregated user data events |
| `blog.user.follow.aggregation` | `UserFollowAggregation` | Aggregated follow data events |

### Kafka Streams

| Stream ID | Source Topics | Sink Topics | Description |
|---|---|---|---|
| `profile-service-streams` | `blog.user.aggregation`, `blog.comment.event`, `blog.post.event` | `blog.user.aggregation` | Merges partial data streams and enriches with local user projections |
| `profile-service-streams` | `blog.user.follow.aggregation` | `blog.user.follow.aggregation` | Deduplicates and enriches follow aggregation events |
| `profile-service-streams` | `blog.user.status` | `blog.user.status` | Deduplicates and enriches presence status events |
| `profile-service-streams` | `blog.user.status` | `users.online` | Aggregates connect/disconnect events into online user count gauge |

**Stream Properties**: `exactly_once_v2` processing, 5s commit interval, batch listener (300 max poll records, 3s poll timeout)

**Queryable Store**: `users-online-store` — exposes the `users.online` Micrometer gauge, queried by `UsersOnline` component.

## Inter-Service Communication

### gRPC (Server)

| RPC Method | Request | Response | Description |
|---|---|---|---|
| `IsUserExist` | `UserExistenceRequest` (single UUID) | `UserExistenceResponse` (bool) | Check if a single enabled user exists |
| `IsUsersExist` | `UsersExistenceRequest` (UUID list) | `UserExistenceResponse` (bool) | Check if any of the provided enabled users exist |
| `GetUserBlacklist` | `UserBlacklistRequest` (UUID bytes) | `UserBlacklistResponse` (UUID list) | Get all blacklisted user IDs for a given user |

**Channel Configuration**:
- Port: `9090` (gRPC)
- Security: All requests permitted (no authentication at gRPC layer)

### gRPC (Client)

| Channel Name | Target Service | Purpose |
|---|---|---|
| `profile-channel` | `profile-service:9090` | Validate user existence before follow operations, check blacklists |

*Used by*: `content-service`, `comment-service`, `community-service`

### Kafka (Event-Driven)

| Direction | Topic | Event | Used By |
|---|---|---|---|
| Inbound | `blog.keycloak.event` | `UserEvent` | Projection service — creates/updates local user records |
| Inbound | `blog.keycloak.event.admin` | `UserEvent` | Admin projection service — handles ban/unban events |
| Inbound | `blog.user.aggregation` | `UserAggregation` | Kafka Streams — enriches partial user data with local projections |
| Inbound | `blog.user.follow.aggregation` | `UserFollowAggregation` | Kafka Streams — deduplicates and enriches follow data |
| Inbound | `blog.user.status` | `ActivityEvent` | Kafka Streams — tracks online users |
| Outbound | `blog.user` | `UserEvent` | `content-service`, `comment-service` |
| Outbound | `blog.user.event` | `UserEvent` | `report-service`, `notification-service` |
| Outbound | `blog.user.follow` | `UserFollowEvent` | `content-service`, `feed-service` |
| Outbound | `blog.user.status` | `ActivityEvent` | `notification-service` |
| Outbound | `blog.user.aggregation` | `UserAggregation` | `feed-service`, `notification-service` |
| Outbound | `blog.user.follow.aggregation` | `UserFollowAggregation` | `feed-service`, `notification-service` |