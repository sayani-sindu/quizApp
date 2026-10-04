# QuizApp — Spring Cloud Microservices + Spring AI

A **microservices-based quiz platform** built with the modern Spring ecosystem: Spring Boot 4, Spring Cloud (Eureka, Gateway, OpenFeign, Config, Resilience4j), Spring AI, PostgreSQL (+pgvector), with full observability. Built to be **free to run locally** (Ollama for AI, no paid APIs required).

---

## Architecture

```mermaid
flowchart LR
    Client[Browser / API Client] --> GW[API Gateway :8765]

    subgraph Infrastructure
        Eureka[Service Registry (Eureka) :8761]
        Config[Config Server :8888]
    end

    GW --> QUIZ[quiz-service :8090]
    GW --> QSVC[question-service :8081]

    QUIZ -- Feign + Circuit Breaker --> QSVC
    QUIZ -- Feign + Circuit Breaker --> AI[ai-service :8095]

    AI --> OLLAMA[(Ollama :11434)]
    QSVC --> PGDATA[(PostgreSQL questiondb + pgvector)]
    QUIZ --> PGDATA2[(PostgreSQL quizdb)]

    Eureka -. registers .-> GW
    Eureka -. registers .-> QUIZ
    Eureka -. registers .-> QSVC
    Eureka -. registers .-> AI
    Config -. config .-> QUIZ
    Config -. config .-> QSVC
    Config -. config .-> AI
    Config -. config .-> GW

    AI -. metrics/traces .-> OBS(Observability: Prometheus / Grafana / Jaeger)
    QUIZ -. metrics/traces .-> OBS
    QSVC -. metrics/traces .-> OBS
```

## Services

| Service | Port | Database | Role |
|---|---|---|---|
| `service-registry` | 8761 | — | Netflix Eureka server (service discovery) |
| `api-gateway` | 8765 | — | Spring Cloud Gateway (WebFlux), routes, authn, tracing |
| `config-server` | 8888 | — | Centralized, git-backed configuration |
| `question-service` | 8081 | `questiondb` (+pgvector) | Question CRUD, quiz question generation, scoring |
| `quiz-service` | 8090 | `quizdb` | Quiz lifecycle, delegates to question-service via OpenFeign |
| `ai-service` | 8095 | — | Spring AI: quiz generation, explanations, grading (Ollama) |

## Tech stack

- **Java 21** · **Spring Boot 4.1** · **Spring Cloud 2025.1** · Maven
- Service discovery: **Netflix Eureka**
- Inter-service calls: **OpenFeign** + **Resilience4j** circuit breakers
- Gateway: **Spring Cloud Gateway** (reactive)
- Config: **Spring Cloud Config Server** (git-backed)
- AI: **Spring AI 2.x** with **Ollama** (llama3.2 / nomic-embed-text), swappable to OpenAI/Anthropic
- Data: **PostgreSQL**, **pgvector** (RAG / semantic search), **Flyway** migrations
- Observability: **Micrometer + OpenTelemetry**, Prometheus, Grafana, Jaeger
- Security: **Keycloak** (OAuth2 / JWT) at the gateway
- Testing: Testcontainers, WireMock, GitHub Actions CI

## Prerequisites

- **JDK 21** — the projects target Java 21. Your default `java` may be another version; set `JAVA_HOME` accordingly (macOS Homebrew example below).
- **PostgreSQL** running locally (or via Docker).
- **Ollama** (Phase 3+) for local AI models.
- Maven is not required — each service ships the Maven Wrapper (`./mvnw`).

## Running the stack

> ⚠️ **JDK 21 is required.** Example on macOS with Homebrew:
> ```bash
> export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.12.1/libexec/openjdk.jdk/Contents/Home
> ```

Start services **in dependency order** (each in its own terminal):

```bash
cd service-registry && ./mvnw spring-boot:run   # 8761
cd config-server    && ./mvnw spring-boot:run   # 8888
cd api-gateway      && ./mvnw spring-boot:run   # 8765
cd question-service && ./mvnw spring-boot:run   # 8081
cd quiz-service     && ./mvnw spring-boot:run   # 8090
cd ai-service       && ./mvnw spring-boot:run   # 8095
```

Verify:
- Eureka dashboard: http://localhost:8761 — all services registered
- Health: http://localhost:8765/actuator/health

## API overview

Everything is reachable through the gateway (`http://localhost:8765`):

| Method | Path | Description |
|---|---|---|
| `GET` | `/question-service/question/allQuestions` | All questions |
| `GET` | `/question-service/question/category/{topic}` | Questions by category |
| `POST` | `/question-service/question/add` | Add a question |
| `POST` | `/quiz-service/quiz/create` | Create a quiz from a category |
| `GET` | `/quiz-service/quiz/get/{id}` | Get quiz questions (no answers) |
| `POST` | `/quiz-service/quiz/submit/{id}` | Submit answers, get score |
| `POST` | `/ai-service/ai/generate` | AI-generated quiz questions |
| `POST` | `/ai-service/ai/explain` | AI explanation for an answer |

## Roadmap

- [x] **Phase 0** — Repo foundation, README, gitignore, push to GitHub
- [ ] **Phase 1** — Config Server, Resilience4j, explicit gateway routes, Flyway, profiles
- [ ] **Phase 2** — Observability: OTel tracing, Prometheus metrics, Grafana, Jaeger
- [ ] **Phase 3** — Spring AI service (Ollama): quiz generation, explanations, grading
- [ ] **Phase 4** — RAG + pgvector semantic search + study assistant
- [ ] **Phase 5** — Security with Keycloak (OAuth2/JWT, roles)
- [ ] **Phase 6** — Testcontainers, WireMock, CI/CD, Docker/Kubernetes
- [ ] **Phase 7** — Docs polish, OpenAPI, Postman collection

## License

MIT