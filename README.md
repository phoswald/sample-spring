
# sample-spring

Experiments with several Spring Framework features in a modern enterprise/AI context:

- Spring Boot
- Spring Modulith
- Spring Data JPA
- Spring for Apache Kafka
- Spring Security
- Spring AI
- ...

Along with integrations of other popular systems in the enterprise/AI world:

- Docker
- Postgres
- Kafka
- Keycloak
- ...

## Development

~~~
$ mvn clean verify
$ mvn spring-boot:run
$ java -jar target/sample-spring-0.0.1-SNAPSHOT.jar
~~~

URLs:

- http://localhost:8080/ (Login as test:test in realm sample)

## Integrations

### Keycloak

~~~
$ docker compose -f integrations/keycloak/compose.yaml up
$ ./integrations/keycloak/setup.sh
~~~

URL: http://localhost:8091/ (Login as admin:admim in realm master)
