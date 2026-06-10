# API Documentation & Deployment Instructions

This guide provides the REST API specifications and setup instructions for running the **Mithila Royal Paan** full-stack application.

---

## 1. REST API Specifications

All endpoints are hosted relative to `/api/v1`.

### Authentication Module (`/api/v1/auth`)

| Method | Endpoint | Auth | Request Payload | Description |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/login` | Public | `LoginRequest` | Verifies credentials, returns JWT token, email, name, role. |
| **POST** | `/register` | Public | `RegisterRequest` | Registers new customer accounts. Hashes passwords. |
| **GET** | `/me` | JWT | None | Fetches the authenticated user profile. |

*Google Social Login flow is mapped to `/oauth2/authorization/google` (handled by Spring Security client).*

### Wedding & Event Bookings (`/api/v1/bookings`)

| Method | Endpoint | Role Allowed | Query / Payload | Description |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/` | CUSTOMER | `BookingRequest` | Places a new event booking. Triggers notifications. |
| **GET** | `/` | CUSTOMER | None | Gets all event bookings placed by the caller. |
| **GET** | `/all` | ADMIN/STAFF | None | Lists all bookings in the system. |
| **PUT** | `/{id}/status` | ADMIN/STAFF | `status` (Query Param) | Approves, Rejects, or Completes a booking. |
| **PUT** | `/{id}/assign` | ADMIN | `staffId` (Query Param) | Assigns a staff coordinator to a booking. |

### Orders & Product Purchases (`/api/v1/orders`)

| Method | Endpoint | Role Allowed | Query / Payload | Description |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/` | CUSTOMER | `OrderRequest` | Creates a product purchase. Calculates total cost. |
| **GET** | `/` | CUSTOMER | None | Returns the user's order history. |
| **GET** | `/all` | ADMIN/STAFF | None | Lists all orders. |
| **PUT** | `/{id}/status` | ADMIN/STAFF | `status` (Query Param) | Updates shipping status (SHIPPED, DELIVERED, CANCELLED). |

### B2B Export Leads (`/api/v1/export-enquiries`)

| Method | Endpoint | Role Allowed | Query / Payload | Description |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/` | Public | `ExportEnquiryRequest` | Submits a bulk export lead. |
| **GET** | `/all` | ADMIN/STAFF | None | Returns list of export enquiries. |
| **PUT** | `/{id}/status` | ADMIN/STAFF | `status` (Query Param) | Sets lead status (CONTACTED, CLOSED). |

### Content Management (Public & Admin CRUD)

#### Products (`/api/v1/products`)
- **GET** `/`: Returns available items (Public).
- **GET** `/{id}`: Fetch product details (Public).
- **GET** `/admin/all`: List all items for dashboard (ADMIN/STAFF).
- **POST** `/admin`: Add a new product (ADMIN/STAFF).
- **PUT** `/admin/{id}`: Update product fields (ADMIN/STAFF).
- **DELETE** `/admin/{id}`: Remove product entry (ADMIN).

#### Blogs (`/api/v1/blogs`)
- **GET** `/`: Fetch all published blogs (Public).
- **GET** `/{id}`: Fetch a detailed article (Public).
- **POST** `/admin`: Publish a new blog (ADMIN/STAFF).
- **PUT** `/admin/{id}`: Update blog fields (ADMIN/STAFF).
- **DELETE** `/admin/{id}`: Delete blog post (ADMIN).

#### Gallery (`/api/v1/gallery`)
- **GET** `/`: Returns media list (Public).
- **POST** `/admin`: Publish a new image (ADMIN/STAFF).
- **DELETE** `/admin/{id}`: Remove image (ADMIN).

#### Testimonials (`/api/v1/testimonials`)
- **GET** `/`: Returns active reviews (Public).
- **POST** `/admin`: Publish client review (ADMIN/STAFF).
- **DELETE** `/admin/{id}`: Remove review (ADMIN).

---

## 2. Local Setup & Deployment Instructions

### Prerequisites
1. **Java Development Kit (JDK)**: Java 17 or Java 21 installed.
2. **Maven**: Maven 3.x installed (or use standard packaging wrappers).
3. **MySQL Database**: Running instance of MySQL (v8.0+ recommended).

### Step 1: Database Setup
Launch your MySQL CLI client or tools like phpMyAdmin / WorkBench and execute:

```sql
CREATE DATABASE mithila_royal_paan;
```

Execute the database tables creation and initial seeds by running the provided SQL script:

```bash
mysql -u root -p mithila_royal_paan < schema.sql
```

*Note: Default users registered during seeding:*
- **Super Admin**: `admin@mithilaroyalpaan.com` / `Admin@123`
- **Staff member**: `staff@mithilaroyalpaan.com` / `Staff@123`
- **Customer account**: `customer@mithilaroyalpaan.com` / `Customer@123`

### Step 2: Configure Environment
Open `src/main/resources/application.properties` and customize connections:

```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

For **Google OAuth Login** to function, configure GCP Client Credentials:
1. Access Google Developer Console.
2. Generate an OAuth 2.0 Client ID under Credentials.
3. Configure Authorized Redirect URIs to match: `http://localhost:8080/login/oauth2/code/google`
4. Set credentials in properties:
   ```properties
   spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
   ```

### Step 3: Compile & Run the Application
In your project root terminal directory, build the source code using Maven:

```bash
mvn clean compile
```

To run the application locally on port `8080`:

```bash
mvn spring-boot:run
```

Once running, access the user interfaces by browsing:
- **Landing Page**: `http://localhost:8080/index.html`
- **Login Portal**: `http://localhost:8080/login.html`
- **Customer Portal**: `http://localhost:8080/dashboard.html`
- **Admin Desk**: `http://localhost:8080/admin.html`
