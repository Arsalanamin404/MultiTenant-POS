# TenantTable API

TenantTable is a **multi-tenant Restaurant Point of Sale (POS) backend** built with **Spring Boot**, designed for startup restaurants. The project focuses on providing a secure, scalable, and maintainable backend architecture that can evolve into a complete restaurant management platform.

---

## About

TenantTable is being developed with a strong emphasis on **clean architecture**, **security**, and **production-ready practices** rather than rapid feature development.

The goal is to provide the backend foundation for a cloud-based restaurant POS system where each restaurant operates as an independent tenant while sharing the same application infrastructure.

---

## Current Features

* User registration
* JWT authentication (Access & Refresh Tokens)
* Secure HTTP-only Refresh Token cookies
* Email verification using OTP
* Resend verification OTP
* Forgot password with OTP
* Password reset
* Refresh token rotation
* Logout & Logout from all devices
* Global exception handling
* Asynchronous email processing
* Automatic email retry mechanism
* OpenAPI / Swagger API documentation

---

## Architecture

The project follows a layered architecture to promote maintainability, scalability, and separation of concerns.

### Current architectural highlights

* Layered Architecture
* Feature-based Package Structure
* DTO-based API Design
* Repository Pattern
* JWT Authentication
* Refresh Token Rotation
* Global Exception Handling
* Asynchronous Task Processing
* Retry Support for Email Delivery
* RESTful API Design

---

## 🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Spring Retry
* Maven

### Database

* PostgreSQL

### Authentication

* JWT (Access & Refresh Tokens)

### Email

* JavaMailSender
* Thymeleaf
* Spring Async (`@Async`)

### Documentation

* OpenAPI / Swagger

---

## Roadmap

Planned features include:

* Multi-tenant restaurant management
* Restaurant onboarding
* Staff & role management
* Menu management
* Table management
* Order management
* Billing & payments
* Inventory management
* Reporting & analytics

---

## Author

**Mohammad Arsalan Rather**
