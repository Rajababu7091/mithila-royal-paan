# Mithila Royal Paan — Full-Stack Web Application

A full-stack Spring Boot + Vanilla JS application for **Mithila Royal Paan**, featuring customer ordering, wedding/event bookings, B2B export enquiries, admin dashboard, and Google OAuth login.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 · Spring Boot 3.2.5 · Spring Security · JPA |
| Database | MySQL 8.x |
| Auth | JWT + Google OAuth2 |
| Build | Apache Maven 3.9.6 (via Maven Wrapper) |
| Frontend | Vanilla HTML / CSS / JS (served as static files) |

---

## Project Structure

```
mithila-royal-paan/
├── backend/              # Spring Boot Maven application
│   ├── mvnw              # Unix Maven Wrapper (used by Render)
│   ├── mvnw.cmd          # Windows Maven Wrapper
│   ├── .mvn/wrapper/     # Wrapper configuration
│   ├── pom.xml
│   └── src/
├── frontend/             # Static frontend assets
└── docs/                 # API & deployment docs
```

---

## Local Development

### Prerequisites
- Java 17 (JDK)
- MySQL 8.x running locally

### 1. Database Setup

```sql
CREATE DATABASE mithila_royal_paan;
```

Then seed the schema:

```bash
mysql -u root -p mithila_royal_paan < backend/database/schema.sql
```

Default seeded credentials:
| Role | Email | Password |
|---|---|---|
| Admin | admin@mithilaroyalpaan.com | Admin@123 |
| Staff | staff@mithilaroyalpaan.com | Staff@123 |
| Customer | customer@mithilaroyalpaan.com | Customer@123 |

### 2. Configure Environment

Copy the example env file and fill in your values:

```bash
cp .env.example .env
```

Or directly edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mithila_royal_paan
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# Google OAuth (optional for local dev)
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```

### 3. Run Locally

```bash
cd backend
./mvnw spring-boot:run        # Linux / macOS / Render
mvnw.cmd spring-boot:run      # Windows
```

Access the app at `http://localhost:8080`

---

## Deploying to Render

### Service Type: **Web Service**

Render must be configured as a **Java (Maven)** project, not Node.js.

### Step-by-step Render Setup

1. Go to [render.com](https://render.com) → **New** → **Web Service**
2. Connect your GitHub repository
3. Set the following settings:

| Setting | Value |
|---|---|
| **Root Directory** | `backend` |
| **Environment** | `Java` |
| **Build Command** | `./mvnw clean package -DskipTests` |
| **Start Command** | `java -jar target/royalpaan-0.0.1-SNAPSHOT.jar` |

### Environment Variables on Render

Set these in **Render → Environment → Environment Variables**:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<host>:3306/mithila_royal_paan` |
| `SPRING_DATASOURCE_USERNAME` | Your MySQL username |
| `SPRING_DATASOURCE_PASSWORD` | Your MySQL password |
| `JWT_SECRET` | Random 64-char secret string |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret |
| `SPRING_PROFILES_ACTIVE` | `prod` (if using profile-based config) |

### Java Version on Render

Add a `JAVA_VERSION` environment variable on Render:

| Variable | Value |
|---|---|
| `JAVA_VERSION` | `17` |

> **Note:** Render auto-detects Java projects when a `pom.xml` is present in the root directory.
> Since `pom.xml` is inside `backend/`, set **Root Directory = `backend`** in Render settings.

### Render `render.yaml` (optional — Infrastructure as Code)

You can add a `render.yaml` file at the project root for automatic deployment configuration:

```yaml
services:
  - type: web
    name: mithila-royal-paan-backend
    env: java
    rootDir: backend
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: java -jar target/royalpaan-0.0.1-SNAPSHOT.jar
    envVars:
      - key: JAVA_VERSION
        value: 17
      - key: SPRING_DATASOURCE_URL
        sync: false
      - key: SPRING_DATASOURCE_USERNAME
        sync: false
      - key: SPRING_DATASOURCE_PASSWORD
        sync: false
      - key: JWT_SECRET
        generateValue: true
```

---

## API Endpoints Overview

Full API documentation is available in [`docs/api_docs_and_deployment.md`](docs/api_docs_and_deployment.md).

| Module | Base Path |
|---|---|
| Authentication | `/api/v1/auth` |
| Orders | `/api/v1/orders` |
| Bookings | `/api/v1/bookings` |
| Export Enquiries | `/api/v1/export-enquiries` |
| Products | `/api/v1/products` |
| Blogs | `/api/v1/blogs` |
| Gallery | `/api/v1/gallery` |
| Testimonials | `/api/v1/testimonials` |

---

## License

© Mithila Royal Paan. All rights reserved.
