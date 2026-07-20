# TenantTable API

TenantTable is a **multi-tenant Restaurant Point of Sale (POS) backend** built with **Spring Boot**, designed for startup restaurants. The project focuses on providing a secure, scalable, and maintainable backend architecture that can evolve into a complete restaurant management platform.

---

## About

TenantTable is being developed with a strong emphasis on **clean architecture**, **security**, and **production-ready practices** rather than rapid feature development.

The goal is to provide the backend foundation for a cloud-based restaurant POS system where each restaurant operates as an independent tenant while sharing the same application infrastructure.

---
# Current Features

## Authentication & Security

* User Registration
* Secure JWT Authentication (Access & Refresh Tokens)
* HTTP-only Refresh Token Cookies
* Refresh Token Rotation
* Logout
* Logout from All Devices
* Email Verification using OTP
* Resend Verification OTP
* Forgot Password using OTP
* Password Reset
* Role-based Authorization
* Spring Security Integration

---

## Tenant Management

* Restaurant (Tenant) Onboarding
* Tenant Profile Management
* Complete Tenant Isolation
* Owner Account Creation
* Tenant-specific Data Access

---

## Category Management

* Create Category
* Update Category
* Delete Category
* Get Category by ID
* List All Categories
* Tenant-specific Categories

---

## Menu Management

* Create Menu Item
* Update Menu Item
* Delete Menu Item
* Get Menu Item by ID
* List Menu Items
* Update Item Availability
* Category Association
* Tenant-specific Menu Isolation

---

## Dining Table Management

* Create Dining Tables
* Update Dining Tables
* Delete Dining Tables
* Get Table by ID
* List Dining Tables
* Unique Table Numbers per Tenant
* Table Status Management
* Tenant-specific Table Isolation

---

## Order Management

* Create Orders
* Get Order by ID
* List Orders
* Update Order Status
* Add Order Items
* Remove Order Items
* Order Total Calculation
* Dining Table Association
* Tenant-specific Order Isolation

---

## Infrastructure

* Layered Architecture
* Feature-based Package Structure
* DTO-based API Design
* Repository Pattern
* Mapper Layer
* Bean Validation
* Global Exception Handling
* Asynchronous Email Processing
* Automatic Email Retry Mechanism
* OpenAPI / Swagger Documentation
* RESTful API Design

---

## Architecture

The project follows a clean, modular architecture focused on scalability and maintainability.

### Architectural Highlights

* Layered Architecture
* Feature-based Package Structure
* DTO-based API Design
* Repository Pattern
* Service Layer
* Mapper Layer
* Spring Security
* JWT Authentication
* Refresh Token Rotation
* Multi-tenant Architecture
* Validation Layer
* Global Exception Handling
* Asynchronous Processing
* Retry Support
* RESTful API Design

---

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Spring Validation
* Spring Retry
* Spring Async
* Maven

### Database

* PostgreSQL

### Authentication

* JWT (Access & Refresh Tokens)

### Email

* JavaMailSender
* Thymeleaf
* Spring Async
* Spring Retry

### Documentation

* OpenAPI 3
* Swagger UI

---

## Upcoming Modules

* Billing & Payments
* Staff Management
* Inventory Management
* Supplier Management
* Purchase Management
* Discounts & Coupons
* QR Code Table Ordering
* Dashboard & Analytics
* Reports
* Restaurant Settings
* Audit Logs
* Subscription & Plan Management
* Redis Caching
* Docker Support
* CI/CD Pipeline
* Monitoring & Logging

---

## Author

**Mohammad Arsalan Rather**

* [GitHub](https://github.com/Arslanamin404)
* [LinkedIn](https://www.linkedin.com/in/mohammad-arsalan-rather-27628722a)
* [Email](mailto:arsalanrather.dev@gmail.com)
