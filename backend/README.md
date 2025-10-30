# Login Backend Project

This project is a backend implementation of a login system using Spring Boot. It is designed to handle user authentication and manage user data.

## Project Structure

```
login-backend
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── login
│   │   │               ├── LoginBackendApplication.java
│   │   │               ├── controller
│   │   │               │   └── AuthController.java
│   │   │               ├── dto
│   │   │               │   ├── LoginRequest.java
│   │   │               │   └── LoginResponse.java
│   │   │               ├── model
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   └── UserRepository.java
│   │   │               └── service
│   │   │                   └── AuthService.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── data.sql
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── login
│                       └── AuthServiceTests.java
└── README.md
```

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd login-backend
   ```

2. **Build the Project**
   Ensure you have Maven installed, then run:
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   You can run the application using:
   ```bash
   mvn spring-boot:run
   ```

## Usage

At this stage, the backend is not connected to any frontend and is not fully functional. The following components are included:

- **AuthController**: Handles HTTP requests related to authentication.
- **AuthService**: Contains the business logic for authentication.
- **UserRepository**: Interface for CRUD operations on User entities.
- **DTOs**: `LoginRequest` and `LoginResponse` for handling login data.

## Future Work

- Implement the actual authentication logic in `AuthService`.
- Connect the backend to a frontend application.
- Add unit tests for all components.

## License

This project is licensed under the MIT License.