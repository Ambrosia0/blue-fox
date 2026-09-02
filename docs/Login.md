```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant IdP as Identity Provider
    participant Backend
    User->>Frontend: Enter credentials
    Frontend->>IdP: Authenticate(credentials)
    IdP->>IdP: Validate credentials
    IdP->>IdP: Generate JWT
    IdP->>Frontend: JWT
    Frontend->>Backend: JWT
    Backend->>Backend: Validate JWT
    Backend->>Frontend: Request
```