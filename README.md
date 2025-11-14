## 🧩 Core Banking – Microservices with Spring Boot & Spring Cloud
This project implements a **microservices-based backend architecture** built with **Spring Boot 3** for a virtual wallet / core banking ecosystem.
It highlights **containerized deployment, inter-service communication, JWT-based authentication, SQL persistence per service, and asynchronous event processing with RabbitMQ**.

## 🚀 Tech Stack
- **Java 21 + Spring Boot 3**
- **Kubernetes** 
- **Docker & Docker Compose**
- **RabbitMQ** (async event processing)
- **Spring Security + JWT**
- **Spring WebFlux (WebClient)**
- **MySQL**
- **Swagger/OpenAPI** for endpoint documentation
- (Upcoming: Observability with Prometheus, Grafana & OpenTelemetry)

## 📐 Architecture
**Auth Service**
- Centralized JWT issuance, refresh & validation.
- Exposes token introspection endpoints.
- Stateless by design → horizontally scalable.

**Users Service**
- Manages identity, registration, credential verification.
- Own isolated MySQL schema.
- Communicates with Auth for cross-service validation.

**Accounts Service**
- Domain: accounts, balances, ledger entries.
- Strong consistency model using ACID constraints.
- Handles internal balance mutations and validations.
- Own isolated MySQL schema.

**Transactions Service**
- Executes inter-account transfers, rollback-safe.
- Emits domain events to RabbitMQ.
- Implements transaction history.
- Own isolated MySQL schema.

**Notification Service**
- Event-driven consumer of RabbitMQ.
- Sends real-time notifications (email).
- Fully asynchronous & decoupled from transactional workload.

**RabbitMQ**
- Message broker implementing CQRS event separation.
- Guarantees temporal decoupling between write and notify workloads.

## 🔐 Authentication Flow
1. Client authenticates against Auth Service, obtaining JWT.
2. Each microservice uses stateless JWT validation (no shared sessions).
3. Services delegate token validation to Auth via WebClient.

Auth service unifies the generation and validation of all JWT tokens.  
Each microservice delegates token validation to Auth before executing business logic.

## 🛠️ How to Run the Project
1. Clone the repository
git clone https://github.com/fedewagner/core-banking-microservices.git
2. Start the environment with Docker Compose
docker compose --env-file test.env -f docker-compose.yml build 
docker compose --env-file test.env -f docker-compose.yml up -d
This will start:
- auth-service at http://localhost:8081
- users-service at http://localhost:8082
- accounts-service at http://localhost:8083
- transactions-service at http://localhost:8084
- notification-service (no external port)
- RabbitMQ UI at http://localhost:15672
- db-users at http://localhost:3010
- db-accounts at http://localhost:3011
- db-transactions at http://localhost:3012

## 📚 API Documentation
Each microservice exposes a Swagger UI:
- Auth: http://localhost:8081/swagger-ui.html
- Users: http://localhost:8082/swagger-ui.html
- Accounts: http://localhost:8083/swagger-ui.html
- Transactions: http://localhost:8084/swagger-ui.html

## 📌 Next Steps (Roadmap)
- Full observability stack: Micrometer + Prometheus + Loki + Grafana.
- Distributed tracing (OpenTelemetry)
- Introduce API Gateway (Spring Cloud Gateway)

## 👨‍💻 Author
Federico Wagner – Backend Developer
🔗 [LinkedIn](https://www.linkedin.com/in/federicowagner1994/)
