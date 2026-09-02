# Content Service

A microservice for managing blog posts, likes, attachments, and user follow relationships in the blog platform. Provides post search, ranking, and feed personalization capabilities.

## Data Ownership

- **Posts** — blog articles with WYSIWYG (TipTap) content, metadata, and status management
- **Post Likes** — user likes on posts, aggregated via Kafka
- **Post Attachments** — files and media embedded in posts
- **Community Projections** — local cached copies of community data to reduce coupling with community-service
- **User & Community Follows** — user subscription data for personalized content feeds

## Responsibilities

### Community Projections Management
- Maintaining local projections of communities via Kafka events to decouple from external services;
- Synchronizing community changes (create/update/delete) through event-driven updates.

### Post Management
- Providing a REST API for creating, editing, and publishing posts via TipTap WYSIWYG editor;
- Providing post search API using PostgreSQL (TRGM) and Elasticsearch (full-text search);
- Supporting advanced filtering, sorting, and scoring of search results;
- Aggregating post view counts using Kafka Streams (exactly-once processing);
- Aggregating post like counts using Redis.

### Follow Management
- Providing an API for managing user-to-user subscriptions;
- Storing data required for building personalized content feeds;
- Providing ranking and filtering data for the user feed.

### Attachments Management
- Binding file attachments to posts;
- Two-step attachment confirmation flow (upload → validate);
- Validating and managing post attachment lifecycle;
- Batch cleanup of orphaned attachments via scheduled tasks.

## Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL (via Spring Data JDBC)
- **Search**: Elasticsearch (full-text) + PostgreSQL (TRGM)
- **Cache**: Redis
- **Message Broker**: Kafka (events + Kafka Streams with exactly-once semantics)
- **gRPC**: Inter-service communication (client only)
- **Auth**: OAuth2 / JWT (Keycloak)
- **Storage**: S3-compatible (for post attachments)
- **DB Migrations**: Liquibase

## Configuration

### Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection string | *required* |
| `DB_USER` | PostgreSQL username | *required* |
| `DB_PASSWORD` | PostgreSQL password | *required* |
| `ELASTIC_URIS` | Elasticsearch connection URI | *required* |
| `REDIS_URL` | Redis connection URL | *required* |
| `S3_ENDPOINT` | S3-compatible storage endpoint | *required* |
| `S3_REGION` | S3 region | `aws-global` |
| `S3_BUCKET` | S3 bucket name for attachments | `publicbucket` |
| `S3_ACCESS_KEY` | S3 access key | *required* |
| `S3_SECRET_KEY` | S3 secret key | *required* |
| `OIDC_ISSUER_URL` | OAuth2/OIDC issuer URL | *required* |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers | *required* |

### Profiles

| Profile | Description |
|---|---|
| `test` | Disables Kafka Streams auto-startup, enables debug logging |
| `es-disabled` | Disables Elasticsearch (for testing without ES) |

## API Endpoints

### Public (No Authentication)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/public/post/{postId}` | Get a single post by ID (includes like status if authenticated) |
| `GET` | `/api/public/post` | Search and list post previews with filtering, sorting, and scoring |

### User (Authenticated)

| Method | Endpoint | Description |
|---|---|---|
| `DELETE` | `/api/user/post/{id}` | Delete a post (user's own or community-moderated) |
| `POST` | `/api/user/post/{id}/like` | Like a post |
| `DELETE` | `/api/user/post/{id}/like` | Unlike a post |
| `GET` | `/api/user/post/{id}/like` | Check if user has liked a post |
| `POST` | `/api/me/follow/user/{userId}` | Follow a user |
| `DELETE` | `/api/me/follow/user/{userId}` | Unfollow a user |
| `GET` | `/api/me/follow/user` | Get the user's follow list (paginated) |
| `POST` | `/api/me/post/{postId}/attachment` | Upload a file attachment for a post |
| `POST` | `/api/me/post/{postId}/attachment/{attachmentId}` | Confirm/validate uploaded attachment |
| `GET` | `/api/me/post/{postId}/attachment` | List attachments for a post |
| `DELETE` | `/api/me/post/{postId}/attachment/{attachmentId}` | Delete an attachment from a post |

### Admin

| Method | Endpoint | Description |
|---|---|---|
| `DELETE` | `/api/admin/post/{id}` | Delete any post (admin-only) |
| `GET` | `/api/admin/post/{id}` | Get any post by ID (admin-only, no access restrictions) |

## Kafka Topics

### Inbound Events (Consumed)

| Topic | Description |
|---|---|
| `blog.community` | Community events (create/update/delete) for maintaining local projections |

### Outbound Events (Produced)

| Topic | Event Type | Description |
|---|---|---|
| `blog.post` | `PostEvent` | Post creation/deletion events for projection synchronization |
| `blog.post.like` | `PostLikeNotification` | Post like/unlike notifications |
| `blog.post.preview` | `PostPreviewEvent` | Post preview events for feed synchronization |
| `blog.post.view` | `PostViewEvent` | Post view events for view count aggregation |
| `blog.user.follow` | `UserFollowEvent` | User follow/unfollow events |

### Kafka Streams

| Stream ID | Source Topic | Sink Topic | Description |
|---|---|---|---|
| `content-service-streams` | `blog.post.view` | `blog.post.preview` | Aggregates post view counts for feed scoring |

**Stream Properties**: exactly-once processing, 5s commit interval

## Inter-Service Communication

### gRPC (Client)

| Channel Name | Target Service | Purpose |
|---|---|---|
| `profile-channel` | `profile-service:9090` | Validate user existence before follow operations |
| `community-channel` | `community-service:9090` | Fetch community data for projections |

### Kafka (Event-Driven)

| Direction | Topic | Event | Used By |
|---|---|---|---|
| Outbound | `blog.post` | `PostEvent` | `comment-service` |
| Outbound | `blog.post.like` | `PostLikeNotification` | `notification-service` |
| Outbound | `blog.post.preview` | `PostPreviewEvent` | `feed-service` |
| Outbound | `blog.post.view` | `PostViewEvent` | Kafka Streams (view aggregation) |
| Outbound | `blog.user.follow` | `UserFollowEvent` | `feed-service`, `notification-service` |
| Inbound | `blog.community` | `CommunityEvent` | Maintains local community projections |