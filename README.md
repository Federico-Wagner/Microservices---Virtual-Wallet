## 🧩 Core Banking – Microservices with Spring Boot & Spring Cloud
This project is a **microservices architecture** built with **Spring Boot** and **Spring Cloud** to simulate the backend of a virtual wallet / fintech system.
The goal is to demonstrate concepts such as **Service Discovery, JWT Security, Inter-service Communication, and Container Deployment**.

## 🚀 Main Technologies
- **Java 21 + Spring Boot 3**
- **Spring Cloud Netflix (Eureka)**
- **Spring Security + JWT**
- **Spring WebFlux (WebClient)**
- **PostgreSQL / MongoDB**
- **Docker & Docker Compose**
- **Swagger/OpenAPI** for endpoint documentation

## 📐 Architecture
flowchart LR
    A[Auth Service] -->|JWT| B[Users Service]
    A -->|JWT| C[Transactions Service]
    B -->|REST| C
    A & B & C --> D[Eureka Server]
    
Eureka Server: Microservice registry.
Auth Service: Login, credential validation, JWT token issuance.
Users Service: User CRUD operations.
Transactions Service: Simulated transfers and transaction history.

🛠️ How to Run the Project
1. Clone the repository
bash
Copy code
git clone https://github.com/fedewagner/core-banking-microservices.git
cd core-banking-microservices
2. Start with Docker Compose
bash
Copy code
docker-compose up -d
This will start:

eureka-server at http://localhost:8761

auth-service at http://localhost:8081

users-service at http://localhost:8082

transactions-service at http://localhost:8083

📚 API Documentation
Each microservice exposes a Swagger UI:

Auth: http://localhost:8081/swagger-ui.html

Users: http://localhost:8082/swagger-ui.html

Transactions: http://localhost:8083/swagger-ui.html

👉 Import the Postman collection included in /postman/collection.json to test the endpoints.

🔐 Authentication Flow
The user logs in to the Auth Service with their DNI and password.
Auth generates a signed JWT and returns it.
The JWT is used in headers (Authorization: Bearer <token>) to consume endpoints from Users and Transactions.
Each service validates the token against Auth before processing the request.

🌐 Deployment (optional if published)
This project is deployed at:

Public Swagger: https://core-banking.onrender.com/swagger-ui.html

Eureka Dashboard: https://core-banking-eureka.onrender.com

📌 Next Steps (Roadmap)

Implement API Gateway with Spring Cloud Gateway.

Integrate Kafka for transaction events.

Observability: Micrometer + Prometheus + Grafana.

👨‍💻 Author
Federico Wagner – Backend Developer
🔗 [LinkedIn](https://www.linkedin.com/in/federicowagner1994/) | [GitHub](https://github.com/Federico-Wagner)
