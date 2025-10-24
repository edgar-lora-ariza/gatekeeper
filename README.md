# 🔐 Gatekeeper

**OAuth2 Authorization Server** built with Spring Boot and Vaadin, providing secure authentication and authorization services with modern UI and comprehensive observability.

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vaadin](https://img.shields.io/badge/Vaadin-24.9.3-blue.svg)](https://vaadin.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Architecture](#-architecture)
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [Security](#-security)
- [Observability](#-observability)
- [Contributing](#-contributing)

---

## ✨ Features

### 🔒 OAuth2 Authorization Server
- **Full OAuth2/OIDC compliance** with Spring Security Authorization Server
- **Multiple grant types**: Authorization Code, Client Credentials, Refresh Token, Device Code, Token Exchange
- **PKCE support** (Proof Key for Code Exchange) for enhanced security
- **JWT tokens** with ES256/RS256 signing algorithms
- **Key rotation** with active key management
- **DPoP support** (Demonstrating Proof-of-Possession)

### 🎨 Modern UI
- **Vaadin-based** login interface with Lumo Dark theme
- **OAuth2 social login** (Google integration ready)
- **Responsive design** optimized for desktop and mobile
- **Production-ready** with optimized frontend build

### 🔑 Key Management
- **Cryptographic key rotation** with seamless transition
- **Multiple key support** for backward compatibility
- **PEM format** for easy key import/export
- **JWK Set endpoint** for token validation

### 👥 User Management
- **Internal authentication** (username/password)
- **External authentication** (OAuth2/OIDC providers)
- **Role-based access control** (RBAC)
- **User profiles** with authority management

### 📊 Observability
- **OpenTelemetry integration** for traces, metrics, and logs
- **Micrometer metrics** exported to OTLP endpoint
- **W3C Trace Context** propagation
- **Actuator endpoints** for health and metrics

---

## 🛠 Tech Stack

### Backend
- **Java 25** with Virtual Threads
- **Spring Boot 3.5.6**
- **Spring Security OAuth2 Authorization Server**
- **Spring Data JPA** with PostgreSQL
- **Flyway** for database migrations

### Frontend
- **Vaadin 24.9.3** with Flow
- **Lumo theme** with dark variant
- **Responsive layouts** and components

### Infrastructure
- **PostgreSQL 14.19** (Alpine)
- **Docker Compose** for local development
- **Gradle 9.1** build system

### Observability
- **OpenTelemetry** (traces, metrics, logs)
- **Micrometer** for metrics collection
- **Spring Boot Actuator**

### Security
- **BouncyCastle** for cryptographic operations
- **JWT** with ES256/RS256 signing
- **PKCE** flow support

---

## 📦 Prerequisites

- **Java 25** or higher
- **Docker** and Docker Compose
- **Gradle 9.1+** (wrapper included)
- **PostgreSQL 14+** (via Docker Compose)

---

## 🚀 Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/gatekeeper.git
cd gatekeeper
```

### 2. Start PostgreSQL

```bash
docker compose up -d
```

This starts PostgreSQL on port `5431` (mapped from container's `5432`).

### 3. Build the project

```bash
./gradlew clean build
```

### 4. Run the application

**Development mode:**
```bash
./gradlew bootRun
```

**Production mode (optimized frontend):**
```bash
# Build Vaadin frontend
./gradlew vaadinBuildFrontend

# Run with production mode
./gradlew bootRun
```

### 5. Access the application

- **Application**: http://localhost:9000/gatekeeper
- **Login UI**: http://localhost:9000/gatekeeper/login
- **API Docs**: http://localhost:9000/gatekeeper/swagger-ui.html
- **Health Check**: http://localhost:9000/gatekeeper/actuator/health
- **OIDC Discovery**: http://localhost:9000/gatekeeper/.well-known/openid-configuration

---

## ⚙️ Configuration

### Environment Variables

Create a `.env` file or set environment variables:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5431
DB_NAME=gatekeeper
DB_USERNAME=postgres
DB_PASSWORD=postgres

# OAuth2 Server
ISSUER_URI=http://localhost:9000/gatekeeper

# Google OAuth2 (Optional)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:9000/gatekeeper/login/oauth2/code/google

# Initial Admin User
ADMIN_CONSOLE_EMAIL=admin@example.com
ADMIN_CONSOLE_PASSWORD=SecurePassword123!

# Initial Signing Key (generate your own)
SIGNING_KEY_IDENTIFIER=initial-key-id
SIGNING_KEY_CERTIFICATE=-----BEGIN CERTIFICATE-----...-----END CERTIFICATE-----
SIGNING_KEY_PRIVATE_KEY=-----BEGIN EC PRIVATE KEY-----...-----END EC PRIVATE KEY-----

# OpenTelemetry (Optional)
OTEL_EXPORTER_OTLP_LOGS_ENDPOINT=http://localhost:4318/v1/logs
OTEL_LOGS_EXPORTER=otlp
```

### IntelliJ IDEA Run Configuration

The project includes a pre-configured run configuration in `.run/Application.run.xml` with:
- All required environment variables
- JVM options for Java 25 compatibility
- Vaadin production mode settings

---

## 🏗 Architecture

Gatekeeper follows **Clean Architecture** (Hexagonal Architecture) principles:

```
┌─────────────────────────────────────────────────┐
│              Controllers (HTTP)                 │
│  ┌───────────────────────────────────────────┐  │
│  │           Use Cases (Business Logic)      │  │
│  │  ┌─────────────────────────────────────┐  │  │
│  │  │      Ports (Interfaces)             │  │  │
│  │  │  ┌───────────────────────────────┐  │  │  │
│  │  │  │  Adapters (Implementations)   │  │  │  │
│  │  │  │  ┌─────────────────────────┐  │  │  │  │
│  │  │  │  │  Repositories (Data)    │  │  │  │  │
│  │  │  │  └─────────────────────────┘  │  │  │  │
│  │  │  └───────────────────────────────┘  │  │  │
│  │  └─────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### Module Structure

#### 📂 Keys Module (`com.bedrock.gatekeeper.keys`)
Manages cryptographic keys for JWT signing and data encryption.

**Key Components:**
- `SigningKey`: JWT signing keys (RSA/EC)
- `EncryptionKey`: AES keys for data encryption
- `GetActiveSigningKeyUseCase`: Retrieve current active key
- `SaveSigningKeyUseCase`: Add new signing key with rotation
- JWK Set endpoint for token validation

#### 📂 Users Module (`com.bedrock.gatekeeper.users`)
Handles user authentication and authorization.

**Key Components:**
- `CustomUser`: Internal user with username/password
- `ExternalUser`: OAuth2/OIDC federated user
- `UserRoles`: SUPER_ADMIN, ADMIN authorities
- OAuth2 integration with Google

#### 📂 Commons (`com.bedrock.gatekeeper.commons`)
Shared utilities and cross-cutting concerns.

**Key Components:**
- `LoginView`: Vaadin-based login UI
- `SecurityService`: Authentication context utilities
- `GlobalExceptionHandler`: Centralized error handling
- Custom security annotations

### Security Filter Chain Order

```
1. OAuth2 Authorization Server  → OAuth2 endpoints, OIDC
2. Actuator                     → Health & metrics
3. OpenAPI                      → Swagger UI
4. Login/Vaadin                 → Login page, UI
5. API                          → REST API with JWT
6. Default                      → Catch-all (permit all)
```

---

## 📚 API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:9000/gatekeeper/swagger-ui.html
```

### OpenAPI Specification
Download OpenAPI JSON:
```
http://localhost:9000/gatekeeper/v3/api-docs
```

### Key Endpoints

#### OAuth2 Endpoints
- `GET /.well-known/openid-configuration` - OIDC Discovery
- `GET /.well-known/oauth-authorization-server` - OAuth2 Metadata
- `GET /oauth2/jwks` - JWK Set (public keys)
- `POST /oauth2/token` - Token endpoint
- `POST /oauth2/authorize` - Authorization endpoint
- `POST /oauth2/introspect` - Token introspection
- `POST /oauth2/revoke` - Token revocation
- `GET /userinfo` - OIDC UserInfo

#### API Endpoints
- `POST /api/account` - Create new account (public)
- `GET /api/keys/signing` - List signing keys (admin)
- `POST /api/keys/signing` - Create signing key (admin)
- `GET /api/users` - List users (admin)

#### Actuator Endpoints
- `GET /actuator/health` - Health check (public)
- `GET /actuator/metrics` - Metrics (admin)

---

## 💻 Development

### Project Structure

```
gatekeeper/
├── src/
│   ├── main/
│   │   ├── java/com/bedrock/gatekeeper/
│   │   │   ├── keys/              # Key management module
│   │   │   ├── users/             # User management module
│   │   │   ├── commons/           # Shared components
│   │   │   ├── configs/           # Spring configurations
│   │   │   └── Application.java   # Main application
│   │   ├── resources/
│   │   │   ├── db/migration/      # Flyway migrations
│   │   │   ├── application.yml    # Application config
│   │   │   └── logback-spring.xml # Logging config
│   │   └── frontend/              # Vaadin frontend
│   └── test/                      # Test classes
├── .run/                          # IntelliJ run configs
├── build.gradle                   # Build configuration
├── compose.yaml                   # Docker Compose
├── CLAUDE.md                      # AI assistant guide
└── README.md                      # This file
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.bedrock.gatekeeper.keys.usecases.SaveSigningKeyUseCaseTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Database Migrations

Migrations are managed by Flyway in `src/main/resources/db/migration/`:

```
V1__create_keys_schema.sql
V2__create_auth_schema.sql
V3__add_initial_admin_user.sql
```

To create a new migration:
1. Create file: `V{version}__{description}.sql`
2. Add SQL statements
3. Restart application (auto-applies on startup)

### Vaadin Development

**Development mode** (hot reload):
```bash
# Set in build.gradle
vaadin {
    productionMode = false
}

./gradlew bootRun
```

**Production mode** (optimized bundle):
```bash
# Set in build.gradle
vaadin {
    productionMode = true
}

./gradlew vaadinBuildFrontend bootRun
```

Frontend resources:
- Components: `src/main/java/com/bedrock/gatekeeper/commons/views/`
- Styles: `src/main/frontend/themes/`
- Static assets: `src/main/resources/static/`

---

## 🔒 Security

### Key Generation

Generate ES256 key pair:
```bash
# Generate private key
openssl ecparam -genkey -name prime256v1 -noout -out private-key.pem

# Generate certificate
openssl req -new -x509 -key private-key.pem -out certificate.pem -days 365
```

Generate RSA key pair:
```bash
# Generate private key
openssl genrsa -out private-key.pem 2048

# Generate certificate
openssl req -new -x509 -key private-key.pem -out certificate.pem -days 365
```

### Security Best Practices

1. **Use strong signing keys** (ES256 recommended)
2. **Rotate keys regularly** (every 90 days)
3. **Enable HTTPS** in production
4. **Use secure passwords** for admin accounts
5. **Keep dependencies updated**
6. **Enable PKCE** for all OAuth2 flows
7. **Monitor security logs**

### Custom Security Annotations

```
@SuperAdminAllowed              // Only super admins
@SuperAdminAndAdminAllowed      // Super admins and admins
```

---

## 📊 Observability

### OpenTelemetry Configuration

```yaml
management:
  otlp:
    metrics:
      export:
        url: http://localhost:4318/v1/metrics
    tracing:
      endpoint: http://localhost:4318/v1/traces
```

### Metrics

Access metrics:
```bash
curl http://localhost:9000/gatekeeper/actuator/metrics
```

Key metrics:
- `http.server.requests` - HTTP request metrics
- `jvm.memory.used` - Memory usage
- `system.cpu.usage` - CPU usage

### Tracing

Traces include:
- HTTP requests
- Database queries
- OAuth2 operations
- Custom `@Observed` methods

### Logs

Logs are exported to OTLP endpoint and include:
- Request/response logs
- Security events
- Application events
- Error traces

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Coding Standards

- Follow **Clean Architecture** principles
- Use **Java 25** features (records, pattern matching, virtual threads)
- Write **unit tests** for use cases
- Document **public APIs** with JavaDoc
- Follow **Spring Boot** best practices

### Git Workflow

- Use **conventional commits**: `feat:`, `fix:`, `docs:`, `refactor:`
- Keep commits **atomic** and focused
- Write **descriptive** commit messages
- Rebase before merging

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Spring Security OAuth2 Authorization Server](https://spring.io/projects/spring-authorization-server)
- [Vaadin](https://vaadin.com/)
- [OpenTelemetry](https://opentelemetry.io/)
- [BouncyCastle](https://www.bouncycastle.org/)

---

## 📞 Support

For questions or issues:
- **GitHub Issues**: [Create an issue](https://github.com/yourusername/gatekeeper/issues)
- **Documentation**: See [CLAUDE.md](CLAUDE.md) for detailed implementation guide
- **Email**: edgar.lora.ariza@gmail.com

---

**Built with ❤️ by the Bedrock team**
