Here’s a clean version that will look good on GitHub.

Just copy everything below and paste it into your README.md:
Markdown# Mission Management System – Backend

Backend API for the Mission Management System.  
It manages employee domestic work missions, multi-level approval workflows, allowances, vehicle requests, mileage claims, settlements, and analytics.

## Overview

The system supports the full domestic work travel process:

- User authentication & authorization (JWT)
- Mission creation and lifecycle management
- Multi-level approval workflow (Function Manager → HRBP → Finance → BizOps → Executive)
- Activity reports (Annex B)
- Vehicle requests (Annex C)
- Mileage claims (Annex D)
- Settlement
- Analytics & exception reporting
- User management (Admin only)

The backend is designed to serve **two clients**:

- Web dashboard (Function Manager, HRBP, Finance, BizOps, Executive, Admin)
- Mobile app (Staff)

## Tech Stack

| Technology          | Version / Notes               |
|---------------------|-------------------------------|
| Language            | Kotlin                        |
| Framework           | Spring Boot 3.4.1             |
| Security            | Spring Security 6.x + JWT     |
| Persistence         | Spring Data JPA / Hibernate   |
| Database            | Microsoft SQL Server          |
| Migration           | Flyway                        |
| Build Tool          | Gradle (Kotlin DSL)           |
| API Documentation   | Swagger / OpenAPI 3           |
| Testing             | JUnit + MockK (planned)       |
| Containerization    | Docker (planned)              |

## Project Structure

```text
src/main/kotlin/com/onemore/mission/
├── auth/               # Login & JWT
├── user/               # User management (Admin)
├── mission/            # Core mission CRUD & status
├── approval/           # Multi-level approval workflow
├── allowance/          # Allowance rates & calculation
├── report/             # Activity Report (Annex B)
├── vehicle/            # Vehicle Request (Annex C)
├── mileage/            # Mileage Claim (Annex D)
├── settlement/         # Settlement
├── analytics/          # Summary, spend, turnaround, exceptions
├── claim/              # General Claim (POSTPONED – do not implement yet)
├── common/             # Shared exceptions, ApiResponse, etc.
├── config/             # Security, OpenAPI, etc.
└── security/           # JWT filter, CustomUserDetails, JwtService
