# Comment Service

A microservice for managing comments, replies, and likes in the blog platform. Handles hierarchical comment trees, attachments, and decoupled projections of external entities.

## Data Ownership

- **Comments** — root and nested comments with hierarchical structure (n-ary tree)
- **Comment Likes** — user likes on comments, aggregated via Kafka
- **Comment Attachments** — file attachments linked to comments
- **Post Projections** — local cached copies of post data to reduce coupling with content-service
- **Community Projections** — local cached copies of community data to reduce coupling with community-service

## Responsibilities

### Local Projections Management
- Maintaining local projections of posts and communities via Kafka events to decouple from external services;
- Synchronizing projection changes through event-driven updates (PostEvent, CommunityEvent);
- Enabling comment creation only against existing posts (validated via local projection).

### Comment Management
- Providing a REST API for creating, retrieving, and deleting comments;
- Storing and managing hierarchical comment structure (n-dimensional tree);
- Supporting paginated root comment lists with sorting (HOT, NEWEST, OLDEST);
- Supporting full tree traversal for comment threads and replies;
- Supporting lazy-loaded replies via `lastSeenId` / `lastSeenInstant` / `lastSeenCount` cursors.

### Comment Likes
- Allowing users to like/unlike comments;
- Aggregating like counts via Kafka events (exactly-once delivery);
- Validating user permissions before allowing likes (checks community membership, bans, etc.).

### Attachments Management
- Binding file attachments to comments;
- Two-step attachment confirmation flow (upload → confirm);
- Validating and managing comment attachment lifecycle;
- Batch cleanup of orphaned attachments via scheduled tasks.

## Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL (via Spring Data JDBC)
- **Cache**: Redis
- **Message Broker**: Kafka (events + consumers)
- **gRPC**: Inter-service communication (client only)
- **Auth**: OAuth2 / JWT (Keycloak)
- **Storage**: S3-compatible (for comment attachments)
- **DB Migrations**: Liquibase

## API Endpoints

### Public (`/api/public`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/post/{postId}/comments` | Get root comments for a post (paginated, sortable) |
| `GET` | `/comment/{commentId}` | Get a single comment with direct replies |
| `GET` | `/comment/{commentId}/tree` | Get full comment tree for a comment |

### User (`/api/user/comment`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | (root) | Create a comment (supports nested replies) |
| `POST` | `/{commentId}/attachment/{attachmentId}` | Confirm attachment upload for a comment |
| `POST` | `/{commentId}/like` | Like a comment |
| `DELETE` | `/{commentId}/like` | Unlike a comment |
| `DELETE` | `/{id}` | Delete a comment (as author/moderator) |

### Admin (`/api/admin/comment`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `DELETE` | `/{id}` | Delete a comment (admin, no auth check) |

## Kafka Topics

| Topic | Direction | Description |
|-------|-----------|-------------|
| `blog.comment` | Outbound | Published on comment creation/deletion events |
| `blog.comment.like` | Outbound | Published on comment like/unlike events (like aggregation) |
| `blog.post` | Inbound | Consumed to maintain local post projections |
| `blog.community` | Inbound | Consumed to maintain local community projections |

## Inter-Service Communication

### gRPC Clients
- **profile-channel** (port 9090) — checks user existence
- **community-channel** (port 9090) — validates community permissions (scope validation)

### Kafka Events (Outbound)
- **blog.comment** — comment creation and deletion events
- **blog.comment.like** — comment like/unlike events (with change deltas)

### Kafka Events (Inbound)
- **blog.post** — consumed to maintain `post_projection` table
- **blog.community** — consumed to maintain `community_projection` table

## Configuration

Key environment variables:

| Variable | Description |
|----------|-------------|
| `DB_URL` | PostgreSQL connection URL |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |
| `REDIS_URL` | Redis connection URL |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker addresses |
| `OIDC_ISSUER_URL` | Keycloak issuer URL for JWT validation |
| `S3_ENDPOINT` | S3-compatible storage endpoint |
| `S3_REGION` | S3 region |
| `S3_ACCESS_KEY` / `S3_SECRET_KEY` | S3 credentials |
| `S3_BUCKET` | Public bucket name for attachments |

## Profiles

- **test** — Disables Kafka Streams auto-startup for integration testing, adds test context to Liquibase

## Building & Running

```bash
# Build
./gradlew comment-service:build

# Run tests
./gradlew comment-service:test

# Docker image
./gradlew comment-service:bootBuildImage
```