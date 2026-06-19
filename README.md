# 🏦 Modern Banking Application

A full-stack, enterprise-grade banking application built to simulate real-world financial operations. This project features a robust Spring Boot backend with a React frontend, deployed live on cloud infrastructure.

## 🌟 Live Demo
* **Frontend:** [https://graceful-yeot-5375af.netlify.app](https://graceful-yeot-5375af.netlify.app)
* **Backend API:** `https://bankingapplication-production-0d2f.up.railway.app/api`

## 🚀 Features
* **Secure Authentication:** JWT-based login and registration system. New users are placed in a `PENDING` state until approved by an administrator.
* **Account Management:** Users can view their balances, account numbers, and status on an interactive dashboard.
* **Core Transactions:** Full support for Deposits, Withdrawals, and user-to-user Transfers with strict balance and overdraft validation.
* **Advanced Financial Tools:**
  * **Fixed Deposits:** Open FDs with calculated interest and penalty logic for early breakage.
  * **Recurring Payments:** Set up automatic background payments using Spring's `@Scheduled` multi-threading.
* **Transaction History & Export:** View chronological transaction logs and export account statements as CSV files.
* **Admin Dashboard:** Administrators can securely approve/reject new users and freeze/unfreeze compromised accounts.

## 🛠️ Technology Stack
### Frontend
* **React.js** (Vite build tool)
* **React Router** for client-side navigation
* **Axios** for API communication
* **Modern CSS** with responsive glassmorphism design

### Backend
* **Java 17** & **Spring Boot 3.2.5**
* **Spring Security** & **JJWT (0.12.6)** for stateless authorization
* **Spring Data JPA / Hibernate** for ORM
* **MySQL (TiDB)** for the production database
* **Lombok** to reduce boilerplate

## 💻 Local Development Setup

### 1. Database Setup
Ensure you have MySQL installed and create a local database.
Alternatively, the project is configured to use a remote TiDB database by passing the `SPRING_DATASOURCE_URL` environment variable.

### 2. Backend Setup
Navigate to the backend directory and run the Spring Boot application:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
*The backend will run on `http://localhost:8080`*

### 3. Frontend Setup
Navigate to the frontend directory, install dependencies, and start the development server:
```bash
cd frontend
npm install
npm run dev
```
*The frontend will run on `http://localhost:5173`*

## 📁 Project Structure
* `/backend` - Contains all Java Spring Boot source code, controllers, services, models, and security configurations.
* `/frontend` - Contains the React UI, components, contexts, and API configurations.

## 🛡️ Security Highlights
* Passwords are irreversibly hashed using **BCrypt**.
* Endpoints are protected via a custom `JwtAuthenticationFilter`.
* Cross-Origin Resource Sharing (CORS) is strictly configured to only allow the designated frontend domains.
* Global exception handling ensures sensitive stack traces are never leaked to the client.

---
*Built as a comprehensive demonstration of full-stack engineering, multi-threading concepts, database management, and RESTful API design.*
