# JobRunr Issue Demo

Hey JobRunr maintainers! This repo contains the demos for 2 issues:

1) When running under PostgreSQL (and MySQL as well), the method `StorageProvider.recurringJobExists` fails when no states are passed as parameters
2) When running under H2, the method `StorageProvider.recurringJobExists` always returns false when no states are passed as parameters

## Requirements

This project requires Java 17 to run, as it was built with Spring Boot 3. It also requires Docker in order to run the test case scenarios which rely on Testcontainers to run PostgreSQL.

## How to reproduce

I've created 2 test case scenarios, one with PostgreSQL and another with H2 to reproduce the issues. To run them, please execute:

```bash
./gradlew test
```
