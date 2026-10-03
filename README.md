# FoodLink — AI-Assisted Food Donation & Matching Platform
FoodLink is a full-stack food donation platform that connects food donors with verified NGOs. It helps donors publish surplus food, enables NGOs to request available donations, and provides administrators with NGO verification capabilities.

The platform combines a Java Spring Boot backend, a PostgreSQL database, a React frontend, Gemini-powered food information extraction, and an optimization-based matching system using the Hungarian algorithm.

## Key Features

* **Food donation management**: Donors can create and manage surplus-food posts.
* **NGO registration and verification**: NGOs can register, while administrators verify accounts before they can request donations.
* **Role-based access control**: Separate permissions for Donors, NGOs, and Admins.
* **Request lifecycle management**: Supports pending, accepted, rejected, and completed requests.
* **Automatic request handling**: Accepting one request matches the food post and rejects competing pending requests.
* **Food expiration**: Expired donations are removed from active availability.
* **AI-assisted food extraction**: Gemini extracts structured donation details from free-text descriptions.
* **Geographic matching**: Uses the Haversine formula to calculate distances between food posts and NGOs.
* **Urgency-aware matching:** Incorporates deterministic urgency/spoilage scoring into assignment costs.
* **Optimized assignment:** Uses the Hungarian algorithm, with a greedy baseline for comparison.
* **Automated testing:** Includes business-rule, service, and algorithm tests using JUnit and Mockito.

## Technology Stack

| Layer             | Technologies                                                       |
| ----------------- | ------------------------------------------------------------------ |
| Language          | Java 17                                                            |
| Backend framework | Spring Boot 4.1.1                                                  |
| Persistence       | Spring Data JPA, Hibernate                                         |
| Security          | Spring Security, HTTP Basic, Role-Based Access Control             |
| Database          | PostgreSQL 17                                                      |
| Frontend          | React 19, JavaScript                                               |
| Frontend tooling  | Vite                                                               |
| AI integration    | Google Gemini 3.6 Flash, Google Java GenAI SDK 1.67.0              |
| Algorithms        | Hungarian assignment algorithm, Greedy baseline, Haversine formula |
| Testing           | JUnit 5, Mockito, Spring Boot test dependencies                    |
| Build             | Maven, npm                                                         |
| Version control   | Git, GitHub                                                        |

## System Architecture

```text
                 React + Vite
                      |
                      | REST API
                      v

             Spring Boot Backend
                      |
         +------------+------------+
         |            |            |
         v            v            v

   Spring Security  Application   Gemini API
         |           Services         |
         |            |               |
         |     +------+-------+       |
         |     |              |       |
         |     v              v       |
         |  Matching       Request    |
         |  Services       Workflow   |
         |     |              |       |
         |     v              |       |
         | Hungarian          |       |
         | Algorithm          |       |
         |     |              |       |
         +-----+--------------+-------+
                      |
                      v

                Spring Data JPA
                      |
                      v

                 PostgreSQL
```

The backend handles authentication, authorization, validation, business rules, request state transitions, and database operations. Gemini assists with extracting structured information from donor descriptions, while the matching algorithm uses deterministic cost calculations.

## User Roles

Donor

* Register and log in.
* Create surplus-food donations.
* Use AI-assisted extraction to prefill donation details.
* View their donations and incoming requests.
* Accept or reject requests.
* Mark accepted donations as completed.

NGO

* Register and log in.
* View available donations.
* Request food from available posts.
* View and track their requests.

NGOs must be verified by an administrator before they can request food.

Admin

* Log in with administrator access.
* View registered NGOs.
* Verify or revoke verification for NGOs.

## Request and FoodPost Lifecycles

### Request lifecycle

                PENDING

               /       \\

              v         v

         ACCEPTED     REJECTED
              |
              v

          COMPLETED

Only pending requests can be accepted or rejected. Only accepted requests can be completed.

### FoodPost lifecycle

PENDING → MATCHED → COMPLETED
   |
   v

EXPIRED

When a donor accepts an NGO's request:

* The selected request becomes `ACCEPTED`.
* The associated FoodPost becomes `MATCHED`.
* Other pending requests for that FoodPost become `REJECTED`.

When an accepted request is completed, the associated FoodPost is marked `COMPLETED`. Pending food posts can also expire based on their availability time.

## Backend Engineering and Business Rules

The Spring Boot backend enforces business rules on the server rather than relying solely on frontend checks.

Key rules include:

* Only verified NGOs can create requests.
* Only pending FoodPosts can be requested.
* Duplicate requests by the same NGO for the same FoodPost are prevented.
* Only the donor who owns a FoodPost can update its requests.
* Only pending requests can be accepted or rejected.
* Only accepted requests associated with matched FoodPosts can be completed.
* Accepting one request rejects competing pending requests.
* Expired donations are no longer available for matching.

Multi-entity state changes, such as accepting a request and updating its associated FoodPost and competing requests, are handled within transactional service methods using Spring's `@Transactional` support.

## Security

FoodLink uses Spring Security with HTTP Basic authentication and role-based authorization.

The backend identifies users through their registered email and resolves their role as one of:

* `ROLE_DONOR`
* `ROLE_NGO`
* `ROLE_ADMIN`

Access to endpoints is restricted by role. The service layer also checks ownership and business permissions before changing records.

Additional controls include:

* Server-side donor ownership validation.
* NGO verification before requesting food.
* Duplicate-request prevention.
* Request and FoodPost state validation.
* Password hashing using BCrypt.

These controls help prevent unauthorized actions even if a client bypasses frontend restrictions.

## Gemini AI Integration

FoodLink integrates Google Gemini 3.6 Flash through the Google Java GenAI SDK.

A donor can enter a free-text description of surplus food. Gemini processes the text and returns structured information using the following fields:

| Field               | Description                                  |
| ------------------- | -------------------------------------------- |
| `foodType`          | Type of food being donated                   |
| `estimatedServings` | Estimated number of servings                 |
| `description`       | Concise summary of the donation              |
| `shelfLifeHours`    | Estimated shelf life in hours                |
| `urgencySignal`     | Advisory urgency level: LOW, MEDIUM, or HIGH |

### AI-assisted workflow

```text
Donor's free-text description
            |
            v

      Gemini API
            |
            v

     Structured JSON
            |
            v

     Backend validation
            |
            v

     Editable draft form
            |
            v

      Donor review
            |
            v

    FoodPost REST API
            |
            v

        Database
```

Gemini is used as an information extraction assistant; it does not directly create or save FoodPosts. The extracted details are presented as an editable draft, and the donor submits the donation through the regular backend endpoint.

The backend validates the extracted fields, including required values, positive servings, and shelf-life limits. If extraction is invalid or unavailable, the donor can use manual entry.

The urgency signal generated by Gemini is advisory and is not used as the numeric urgency score in the deterministic matching algorithm.

## Matching and Optimization

FoodLink implements an assignment-based matching system to associate available food donations with NGO capacity.

The matching cost is based on factors that include:

* Geographic distance between a food post and an NGO, calculated using the Haversine formula.
* Deterministic urgency/spoilage scoring.

NGO capacity is expanded into assignment slots, allowing an NGO to be considered for multiple donations when its capacity permits.

## Hungarian algorithm

The Hungarian algorithm solves the minimum-cost assignment problem across the cost matrix. Unlike a simple local selection strategy, it considers the overall assignment to minimize the total cost.

## Greedy baseline

A greedy matching service is also implemented for comparison. It assigns each food post to the lowest-cost currently unused NGO slot for that step. This is a baseline and does not guarantee the global minimum total assignment cost.

## Algorithm Benchmark

A reproducible benchmark was implemented to compare Hungarian assignment with the greedy baseline using generated cost matrices.

## Benchmark methodology

| Parameter                 |                Value |
| ------------------------- | -------------------: |
| Number of benchmark cases |                  100 |
| Cost matrix size          |              10 × 10 |
| Generated cost range      |                1–100 |
| Random seed               |                   42 |
| Algorithms compared       | Hungarian and Greedy |

## Measured results

| Metric                | Hungarian | Greedy |
| --------------------- | --------: | -----: |
| Total assignment cost |    14,480 | 21,030 |
| Cases with lower cost |        94 |      0 |
| Equal-cost cases      |         6 |      6 |

Overall assignment cost reduction: 31.15%

The reduction is calculated relative to the greedy baseline:

`((21,030 - 14,480) / 21,030) × 100 = 31.15%`

In this benchmark, Hungarian produced a lower total assignment cost in 94 out of 100 cases, while both algorithms produced equal costs in 6 cases.

Benchmark scope: These results were obtained using generated 10 × 10 cost matrices. They demonstrate algorithmic behavior on the benchmark inputs and should not be interpreted as measured reductions in real-world transportation costs, delivery time, or food waste.

## Testing

FoodLink includes automated tests for service-level business rules and matching behavior using JUnit 5 and Mockito.

## RequestService tests

* Preventing unverified NGOs from creating requests.
* Rejecting duplicate requests.
* Accepting a request and matching its FoodPost.
* Automatically rejecting other pending requests for the same FoodPost.
* Preventing unauthorized donors from updating requests.
* Completing accepted requests and their associated FoodPosts.

## Matching tests

* Verifying assignment results.
* Verifying NGO capacity-slot handling.
* Validating matching cost calculations.
* Comparing Hungarian assignment against a greedy baseline.

The benchmark uses a fixed random seed for reproducibility.

Testing supports the correctness of the implemented service rules and algorithm behavior for the cases covered. No production uptime, concurrent-user capacity, or AI extraction accuracy percentage is claimed.

## Database

FoodLink uses PostgreSQL with Spring Data JPA and Hibernate for persistence.

The principal domain entities include:

* Donor: A user who publishes food donations.
* NGO: An organization that requests donations and has a verification status.
* FoodPost: A food donation with details, availability, and lifecycle status.
* Request: A request by an NGO for a particular FoodPost.
* Admin: An administrator who manages NGO verification.

The application uses relational entity associations to represent ownership and request relationships.

For local development, Hibernate schema generation is configured with `ddl-auto=update`. For production deployments, use a managed database migration strategy and review schema changes explicitly.

## Project Structure

The current GitHub repository is the Spring Boot backend.

```text
foodlink/

├── .mvn/

├── src/

│   ├── main/

│   │   ├── java/

│   │   │   └── com/foodlink/foodlink/

│   │   │       ├── config/

│   │   │       ├── controller/

│   │   │       ├── dto/

│   │   │       ├── entity/

│   │   │       ├── repository/

│   │   │       ├── security/

│   │   │       └── service/

│   │   └── resources/

│   └── test/

├── mvnw

├── mvnw.cmd

├── pom.xml

└── README.md
```

The React/Vite frontend currently resides in a sibling local directory named `frontend`, outside this backend repository. Its package uses React, React DOM, React Router, and Vite.

## Prerequisites

Install the following:

* Java 17
* PostgreSQL 17 (or a compatible PostgreSQL version)
* Node.js and npm (for the separate local frontend)
* Git

Maven is not required to be installed globally because the repository includes the Maven wrapper.

## Local Setup

1. Clone the backend repository

```bash
git clone https://github.com/suprithack03/FoodLink.git

cd FoodLink
```

2. Create the PostgreSQL database

Ensure PostgreSQL is running and create a database named `foodlink`.

For example, from `psql`:

```sql
CREATE DATABASE foodlink;
```

The current local development configuration uses PostgreSQL on port `5433`. Update the datasource settings if your local PostgreSQL instance uses a different port.

3. Configure environment variables

Set your database credentials and Gemini API key through environment variables. Do not commit passwords or API keys.

For PowerShell, for the current terminal session:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/foodlink"

$env:DB_USERNAME = "postgres"

$env:DB_PASSWORD = "your_database_password"

$env:GEMINI_API_KEY = "your_gemini_api_key"
```

Configure the backend's `application.properties` to read these values:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5433/foodlink}

spring.datasource.username=${DB_USERNAME:postgres}

spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format_sql=true

gemini.api.key=${GEMINI_API_KEY}

logging.level.org.springframework.security=DEBUG
```

These are development-oriented settings. Avoid verbose SQL and security debug logging in production, and use a secure secrets-management approach for deployed environments.

## 4. Run the backend

On Windows:

```powershell
.\\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The backend uses Spring Boot's configured server port, which is `8080` by default unless changed in application configuration.

## Frontend Setup

The React/Vite frontend is maintained in a separate local `frontend` directory and is not currently included in this backend GitHub repository.

If you have the frontend directory available locally, navigate to it:

```bash
cd ..\\frontend

npm install

npm run dev
```

Vite typically starts at:

`http://localhost:5173`

The frontend must be configured to send API requests to the running backend. Confirm that the backend's CORS configuration permits the frontend origin.

To produce a frontend production build:

```bash
npm run build
```

## API Overview

The backend exposes REST endpoints for the application's core workflows. The following routes are documented at a high level; consult the controllers for the complete request and response schemas.

| Endpoint                             | Purpose                                      | Role                                    |
| ------------------------------------ | -------------------------------------------- | --------------------------------------- |
| `/api/donors`                        | Donor registration and related operations    | Public registration / configured access |
| `/api/ngos`                          | NGO registration and related operations      | Public registration / configured access |
| `/api/food-posts`                    | Food donation management                     | Donor                                   |
| `/api/food-posts/available`          | Retrieve available food posts                | NGO                                     |
| `/api/requests`                      | Create a food request                        | NGO                                     |
| `/api/requests/my`                   | Retrieve the authenticated NGO's requests    | NGO                                     |
| `/api/requests/my-food-posts`        | Retrieve requests for the donor's food posts | Donor                                   |
| `/api/requests/{id}/status`          | Accept or reject a request                   | Donor                                   |
| `/api/admins/ngos/{id}/verification` | Update NGO verification                      | Admin                                   |
| `/api/gemini/extract-food-post`      | Extract structured food information          | Donor                                   |
| `/api/gemini/test`                   | Gemini connectivity test endpoint            | Public in current configuration         |

Protected endpoints require the configured HTTP Basic credentials and appropriate role. Refer to the backend controllers and security configuration for the exact supported methods and payloads.

## Running Tests

Run the complete backend test suite from the repository root.

Windows:

```powershell
.\mvnw.cmd test
```

macOS/Linux:

```bash
./mvnw test
```

The tests cover implemented service rules, request state transitions, matching logic, and the algorithm comparison benchmark.

## Key Engineering Decisions

* Server-side authorization: Role and ownership checks are enforced by the backend rather than relying on the frontend.
* Transactional workflows: Related request and FoodPost state transitions are grouped in transactional service methods.
* AI-assisted, human-reviewed extraction: Gemini generates a structured draft, while donors review details and the backend remains responsible for validation and persistence.
* Deterministic matching: The matching score is separate from Gemini's advisory urgency output, keeping algorithmic assignments reproducible for the same inputs.
* Benchmark-driven algorithm comparison: Hungarian assignment is compared with a greedy baseline using a repeatable generated dataset.
* Relational persistence: PostgreSQL and JPA provide structured storage for users, donations, and requests.

## Future Enhancements

Potential future work includes:

* Adding a production-ready email-based password recovery workflow.
* Improving observability, structured logging, and operational monitoring.
* Adding deployment automation and production configuration.
* Expanding benchmark datasets to include real geographic and capacity distributions.
* Evaluating Gemini extraction quality with a labelled test dataset.
* Adding integration and load testing for realistic deployment conditions.

These are potential enhancements, not claims about currently implemented functionality.

## Resume Summary

FoodLink demonstrates full-stack development using Java, Spring Boot, React, and PostgreSQL, with role-based security, transactional business workflows, AI-assisted structured extraction, geographic matching, and algorithm optimization.

A measured benchmark compared Hungarian assignment with a greedy baseline across 100 generated 10 × 10 cost matrices. Hungarian produced **31.15% lower total assignment cost** and a lower-cost result in **94 of 100 cases** on those generated inputs.

## Repository

GitHub: https://github.com/suprithack03/FoodLink










