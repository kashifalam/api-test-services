# API Test Services & Test Suites

Consumer repository for service SDKs and executable TestNG test suites. Depends on the separately published `api-test-core` framework.

## Prerequisites

1. Install or deploy `api-test-core` to your Maven repository:
   ```bash
   cd ../api-test-core
   mvn clean install
   ```
2. Java 21+
3. Maven 3.9+

## Structure

```
api-test-services/
├── service-sdks/order-service-sdk/   # Order & Payment API clients
├── test-suites/order-service-tests/  # Service-level REST tests
└── test-suites/e2e-workflow-tests/   # REST → JDBC → Redis E2E tests
```

## Run Tests Locally

```bash
# All modules compile
mvn clean compile

# Order service smoke tests against QA
mvn test -pl test-suites/order-service-tests -am -Denv=qa -Dgroups=smoke

# E2E regression
mvn test -pl test-suites/e2e-workflow-tests -am -Denv=qa -Dgroups=e2e
```

## Environment Configuration

Environment YAML files live in each test module under `src/test/resources/environments/`. Secrets use `${ENV_VAR}` placeholders resolved at runtime.

Required environment variables for QA:
- `AUTH_CLIENT_ID`, `AUTH_CLIENT_SECRET`
- `ORDER_DB_USER`, `ORDER_DB_PASSWORD`
- `REDIS_PASSWORD` (if applicable)

## Jenkins

Use the included `Jenkinsfile` with parameters:
- `ENV` — target environment (qa, dev, staging)
- `TEST_SUITE` — module to run
- `GROUPS` — TestNG group filter (smoke, regression, e2e)
- `CORE_REPO_URL` — optional; builds core from source if set
- `CORE_VERSION` — Maven coordinate version for api-test-core

Configure Jenkins credentials: `auth-client-id`, `auth-client-secret`, `order-db-user`, `order-db-password`, `redis-password`.

## Adding a New Service

1. Create `service-sdks/<service>-sdk` module with client + builders.
2. Create `test-suites/<service>-tests` module depending on SDK + core.
3. Add module to root `pom.xml`.
4. Add environment endpoints to test module YAML.
