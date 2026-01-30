# Lernia 

Access the fully deployed and functional version of the Lernia platform here:

👉 https://lernia.vercel.app


## 👥 **Team**

| Nome | GitHub | Contribuição |
|---|---|---|
| Ana Carolina Coutinho | https://github.com/acarolinacc | 20% |
| Maria Vieira | https://github.com/mariajvieira | 20% |
| Samuel Oliveira | https://github.com/samuoliveira13 | 20% |
| Gessica Goulart | https://github.com/gessicagoulart | 20% |
| Felipe Ferreira | https://github.com/fjacobf | 20% |

## Overview

Lernia is a comprehensive web platform designed to help students explore universities, courses, and scholarships worldwide. The platform provides advanced search and filtering capabilities, user authentication, favorites management, and detailed information about educational institutions and academic programs.

## Documentation

For detailed project documentation, please visit our **[Wiki](https://github.com/MESW-LES-2025/H/wiki)**:

- **[Product Vision](https://github.com/MESW-LES-2025/H/wiki/Product-Vision)** - Project goals, target audience, and value proposition
- **[User Stories](https://github.com/MESW-LES-2025/H/wiki/User-stories)** - Complete list of functional requirements and user scenarios
- **[Acceptance Tests](https://github.com/MESW-LES-2025/H/wiki/Acceptance-Tests)** - Test scenarios and acceptance criteria (ATC)
- **[Domain Analysis](https://github.com/MESW-LES-2025/H/wiki/Domain-Analysis)** - Domain modeling and business logic
- **[System Design](https://github.com/MESW-LES-2025/H/wiki/System-Design)** - System architecture and technical design decisions
- **[User Interface Design](https://github.com/MESW-LES-2025/H/wiki/User-Interface-Design)** - UI/UX specifications and mockups

### Sprint Documentation
- **[Iteration 1](https://github.com/MESW-LES-2025/H/wiki/Iteration-1)** - First sprint deliverables and outcomes
- **[Iteration 2](https://github.com/MESW-LES-2025/H/wiki/Iteration-2)** - Second sprint deliverables and outcomes
- **[Iteration 3](https://github.com/MESW-LES-2025/H/wiki/Iteration-3)** - Third sprint deliverables and outcomes
- **[Iteration 4](https://github.com/MESW-LES-2025/H/wiki/Iteration-4)** - Fourth sprint deliverables and outcomes

> **Note for New Developers:** Start by reading the Product Vision and User Stories to understand the project context before diving into the code.

## Technology Stack

### Frontend
- **Framework:** Angular 20.1.0
- **UI Libraries:** 
  - Bootstrap 5.3.8
  - Bootstrap Icons 1.13.1
  - Font Awesome 7.1.0
  - ng-bootstrap 19.0.1
- **Language:** TypeScript 5.8.2
- **Testing:** Karma + Jasmine
- **Build Tool:** Angular CLI 20.1.5

### Backend
- **Framework:** Spring Boot 3.4.1
- **Language:** Java 21
- **Database:** PostgreSQL 42.7.4
- **ORM:** Spring Data JPA + Hibernate
- **Database Migration:** Flyway 11.1.0
- **Security:** Spring Security (with OAuth2 support)
- **Build Tool:** Maven 3.9
- **Additional Libraries:**
  - Lombok (for reducing boilerplate code)
  - MapStruct (for DTO mapping)
  - Jakarta Validation (for input validation)

### Infrastructure & DevOps
- **Containerization:** Docker + Docker Compose
- **Database:** PostgreSQL 16
- **Database Admin Tool:** pgAdmin 4
- **Testing:** Selenium WebDriver (Firefox) for acceptance tests
- **CI/CD:** GitHub Actions (configured in `.github` folder)

## Project Structure

```
MESW-LES-2025/H/
│
├── frontend/                    # Angular application
│   ├── src/                    # Source code
│   │   ├── app/               # Angular components, services, guards
│   │   ├── assets/            # Static assets (images, styles)
│   │   └── environments/      # Environment configurations
│   ├── package.json           # NPM dependencies
│   └── angular.json           # Angular CLI configuration
│
├── backend/                     # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lernia/auth/
│   │   │   │   ├── controller/      # REST API endpoints
│   │   │   │   ├── service/         # Business logic layer
│   │   │   │   ├── repository/      # Data access layer (JPA)
│   │   │   │   ├── entity/          # Database entities
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── mapper/          # Entity-DTO mappers
│   │   │   │   └── config/          # Security & app configuration
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/migration/    # Flyway SQL migrations
│   │   └── test/               # Unit tests
│   ├── pom.xml                # Maven dependencies
│   └── Dockerfile             # Backend container image
│
├── acceptance-tests/           # End-to-end Selenium tests
│   ├── src/test/java/         # Test classes
│   └── pom.xml                # Test dependencies
│
├── docker-compose.yml          # Container orchestration
├── .github/                    # CI/CD workflows
└── README.md                   # Project documentation
```

## Key Features

### Core Functionality
- **User Authentication:** Register, login (with remember me), and OAuth2 support (Google, GitHub)
- **University Search:** Filter by name, country, location, and cost of living
- **Course Search:** Filter by name, language, type, cost, credits, duration, and area of study
- **Scholarship Discovery:** Browse available scholarships by university
- **Favorites Management:** Save and manage favorite universities and courses
- **Reviews:** Read and submit reviews for universities and courses
- **User Profile:** View and update personal information, location, age, and gender
- **Analytics Dashboard:** View platform statistics and popular items

### Database Schema
The application uses PostgreSQL with the following main entities:
- **Users:** Authentication and profile information
- **Universities:** Institution details, location, contact info
- **Courses:** Academic programs with admission requirements
- **Scholarships:** Financial aid opportunities
- **Reviews:** User feedback for universities and courses
- **Locations:** Geographic data with cost of living
- **Areas of Study:** Academic disciplines

For detailed domain modeling, see the [Domain Analysis Wiki](https://github.com/MESW-LES-2025/H/wiki/Domain-Analysis).

## Getting Started

### Prerequisites
- **Node.js** 18+ and npm (for frontend)
- **Java** 21+ (for backend)
- **Maven** 3.9+ (for backend build)
- **Docker** and **Docker Compose** (for database)
- **Firefox** and **geckodriver** (for acceptance tests)

### Running the Application

#### 1. Start the Database
```bash
docker-compose up -d
```
This starts PostgreSQL on port `5433` and pgAdmin on port `5050`.

#### 2. Run the Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Backend API will be available at `http://localhost:8080`.

#### 3. Run the Frontend
```bash
cd frontend
npm install
ng serve
```
Frontend application will be available at `http://localhost:4200`.

### Running Tests

#### Backend Unit Tests
```bash
cd backend
mvn test
```

#### Frontend Unit Tests
```bash
cd frontend
ng test
```

#### Acceptance Tests
```bash
cd acceptance-tests
export APP_BASE_URL=http://localhost:4200
export HEADLESS=true  # Optional: run tests headless
mvn test
```

See the [Acceptance Tests Wiki](https://github.com/MESW-LES-2025/H/wiki/Acceptance-Tests) for detailed test scenarios and coverage.

## API Documentation

The backend exposes RESTful endpoints for:
- `/api/auth/**` - Authentication and registration
- `/api/users/**` - User profile management
- `/api/universities/**` - University search and details
- `/api/courses/**` - Course search and details
- `/api/scholarships/**` - Scholarship information
- `/api/reviews/**` - Review management
- `/api/favorites/**` - Favorites management
- `/api/analytics/**` - Platform analytics

For complete system architecture and API design, see the [System Design Wiki](https://github.com/MESW-LES-2025/H/wiki/System-Design).

## Database Configuration

### Local Development
Default connection settings (defined in `docker-compose.yml`):
- **Host:** localhost
- **Port:** 5433
- **Database:** lernia
- **Username:** lernia
- **Password:** lernia

### Migrations
Database schema is managed by Flyway. Migration scripts are located in:
```
backend/src/main/resources/db/migration/
```

## Environment Variables

### Backend
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database user
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT tokens (if using JWT)

### Frontend
- Environment-specific configs in `frontend/src/environments/`

### Acceptance Tests
- `APP_BASE_URL` - Frontend URL (default: `http://localhost:4200`)
- `WAIT_SECONDS` - Selenium wait timeout (default: 10)
- `HEADLESS` - Run browser in headless mode (default: false)
- `GECKODRIVER_PATH` - Path to geckodriver executable
- `FIREFOX_BIN` - Path to Firefox binary

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## Development Guidelines

### Code Style
- **Backend:** Follow Java conventions, use Lombok annotations, keep services thin
- **Frontend:** Follow Angular style guide, use TypeScript strict mode

### Testing Requirements
- Write unit tests for services and components
- Add acceptance tests for critical user flows (see [Acceptance Tests Wiki](https://github.com/MESW-LES-2025/H/wiki/Acceptance-Tests))
- Ensure all tests pass before submitting PR

### Database Changes
- Always create Flyway migration scripts for schema changes
- Use versioned migrations (V#__description.sql)
- Test migrations on a clean database

### UI/UX Guidelines
- Follow the design specifications in [User Interface Design Wiki](https://github.com/MESW-LES-2025/H/wiki/User-Interface-Design)
- Maintain consistency with existing components
- Ensure responsive design for mobile devices

## Deployment

The application is deployed on:
- **Frontend:** Vercel (https://lernia.vercel.app)
- **Backend:** [Specify your backend hosting]
- **Database:** [Specify your database hosting]

## License

This project was developed as part of the MESW-LES course, 2025.
For academic and educational purposes.

## Contact

For questions or support, please open an issue in the GitHub repository or check the [Team Wiki](https://github.com/MESW-LES-2025/H/wiki/Team) for contact information.

---

## Quick Start Guide for New Developers

1. **Read the Documentation** (in this order)
   - 📖 [Product Vision](https://github.com/MESW-LES-2025/H/wiki/Product-Vision) - Understand project goals and context
   - 📝 [User Stories](https://github.com/MESW-LES-2025/H/wiki/User-stories) - Learn functional requirements
   - 🏗️ [Domain Analysis](https://github.com/MESW-LES-2025/H/wiki/Domain-Analysis) - Understand business domain
   - 🎨 [User Interface Design](https://github.com/MESW-LES-2025/H/wiki/User-Interface-Design) - Review UI/UX specifications
   - ⚙️ [System Design](https://github.com/MESW-LES-2025/H/wiki/System-Design) - Study architecture decisions

2. **Set Up Your Environment**
   - Follow the "Getting Started" section above
   - Ensure all tests pass: `mvn test` (backend), `ng test` (frontend), acceptance tests
   - Access pgAdmin at `http://localhost:5050` to explore the database

3. **Explore the Codebase**
   - **Frontend entry point:** `frontend/src/app/app.component.ts`
   - **Backend entry point:** `backend/src/main/java/com/lernia/auth/`
   - **Key services:** `CourseService.java`, `UniversityService.java`, `FavoritesService.java`
   - **API controllers:** `backend/src/main/java/com/lernia/auth/controller/`

4. **Review Sprint Progress**
   - Check [Iteration 1-4](https://github.com/MESW-LES-2025/H/wiki/Iteration-1) to understand development history
   - Review completed features and lessons learned

5. **Make Your First Contribution**
   - Check existing issues for "good first issue" labels
   - Review open PRs to see ongoing work
   - Follow the contributing guidelines above

6. **Get Help**
   - Open a discussion for general questions
   - Create an issue for bugs or feature requests
   - Check the Wiki for detailed documentation
   - Contact the team (see [Team Wiki](https://github.com/MESW-LES-2025/H/wiki/Team))

**Happy coding! 🚀**
