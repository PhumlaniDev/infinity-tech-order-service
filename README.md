# Order Service

[![CI Workflow](https://github.com/PhumlaniDev/infinity-tech-order-service/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/PhumlaniDev/infinity-tech-order-service/actions/workflows/ci-cd.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=bugs)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=PhumlaniDev_infinity-tech-order-service&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=PhumlaniDev_infinity-tech-order-service)

## Overview

The `order-service` is a Spring Boot-based microservice responsible for managing order-related operations. It integrates with a PostgreSQL database and provides RESTful APIs for order creation, retrieval, and management.

## Features

- Order management (CRUD operations).
- Integration with SonarCloud for code quality analysis.
- Dockerized for easy deployment.
- CI/CD pipeline with GitHub Actions.
- Static and dynamic security testing workflows.

## Prerequisites

- Java 21
- Maven
- Docker
- PostgreSQL
- GitHub account with necessary secrets configured for CI/CD.

## Getting Started

### Clone the Repository

```bash
  git clone https://github.com/PhumlaniDev/infinity-tech-order-service.git
  cd infinity-tech-order-service
```

### Build the Project

```bash
  mvn clean install -DskipTests
```

### Run the Application

```bash
  mvn spring-boot:run
```

### Run with Docker

```bash
  docker build -t order-service .
  docker run -p 9300:9300 order-service
```

## CI/CD Pipeline
The CI/CD pipeline is configured using GitHub Actions. It includes:
* Checkstyle: Ensures code adheres to style guidelines.
* Build: Compiles and packages the application.
* SAST: Static Application Security Testing.
* SCA: Software Composition Analysis.
* Notifications: Sends status updates to Discord.

## Environment Variables
The following environment variables are required for the application:

- `SPRING_DATASOURCE_URL`: JDBC URL for the PostgreSQL database.
- `SPRING_DATASOURCE_USERNAME`: Database username.
- `SPRING_DATASOURCE_PASSWORD`: Database password.
- `JASYPT_ENCRYPTOR_PASSWORD` : Password for Jasypt encryption.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.